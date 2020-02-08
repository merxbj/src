/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	ctresource.c
 * 
 * @Brief   
 * This file contains the implementation of some generic helper functions.
 *
 * @Author	Liu Chun
 * @Date 	May 15 2008
 * 
 */

#include "ctresource.h"
#include "cthardware.h"
#include "ctutils.h"
#include <linux/err.h>
#include <linux/slab.h>

#define AUDIO_SLOT_BLOCK_NUM 	256

/* Resource allocation based on bit-map management mechanism */
static int 
get_resource(u8 *rscs, unsigned int amount, unsigned int multi, unsigned int *ridx)
{
	int i = 0, j = 0, k = 0, n = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	/* Check whether there are sufficient resources to meet request. */
	for (i = 0, n = multi; i < amount; i++) {
		j = i / 8;
		k = i % 8;
		if (rscs[j] & ((u8)1 << k)) {
			n = multi;
			continue;
		}
		if (!(--n)) {
			break; /* found sufficient contiguous resources */
		}
	}

	if (i >= amount) {
		/* Can not find sufficient contiguous resources */
		return -ENOENT;
	}

	/* Mark the contiguous bits in resource bit-map as used */
	for (n = multi; n > 0; n--) {
		j = i / 8;
		k = i % 8;
		rscs[j] |= ((u8)1 << k);
		i--;
	}

	*ridx = i + 1;

	return 0;
}

static int put_resource(u8 *rscs, unsigned int multi, unsigned int idx)
{
	unsigned int i = 0, j = 0, k = 0, n = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	/* Mark the contiguous bits in resource bit-map as used */
	for (n = multi, i = idx; n > 0; n--) {
		j = i / 8;
		k = i % 8;
		rscs[j] &= ~((u8)1 << k);
		i++;
	}

	return 0;
}

int mgr_get_resource(struct rsc_mgr *mgr, unsigned int n, unsigned int *ridx)
{
	int err = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	if (n > mgr->avail) {
		return -ENOENT;
	}

	if (!(err = get_resource(mgr->rscs, mgr->amount, n, ridx))) {
		mgr->avail -= n;
	}
	
	return err;
}

int mgr_put_resource(struct rsc_mgr *mgr, unsigned int n, unsigned int idx)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	put_resource(mgr->rscs, n, idx);
	mgr->avail += n;

	return 0;
}

static unsigned char offset_in_audio_slot_block[NUM_RSCTYP] = {
	[SRC]		= 0x1, /* SRC channel is at Audio Ring slot 1 every 16 slots. */
	[AMIXER]	= 0x4,
	[SUM]		= 0xc,
};

static int rsc_index(const rsc_t *rsc)
{
    return rsc->conj;
}

static int audio_ring_slot(const rsc_t *rsc)
{
    return ((rsc->conj << 4) + offset_in_audio_slot_block[rsc->type]);
}

static int rsc_next_conj(rsc_t *rsc)
{
	unsigned int i;
	for (i = 0; (i < 8) && (!(rsc->msr & (0x1 << i))); i++);
	rsc->conj += (AUDIO_SLOT_BLOCK_NUM >> i);
	return rsc->conj;
}

static int rsc_master(rsc_t *rsc)
{
	return (rsc->conj = rsc->idx);
}

static struct rsc_ops rsc_generic_ops = {
	.index		= rsc_index,
	.output_slot	= audio_ring_slot,
	.master		= rsc_master,
	.next_conj	= rsc_next_conj,
};

int rsc_init(rsc_t *rsc, u32 idx, RSCTYP type, u32 msr, void *hw)
{
	int err = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	
	rsc->idx = idx;
	rsc->conj = idx;
	rsc->type = type;
	rsc->msr = msr;
	rsc->hw = hw;
	rsc->ops = &rsc_generic_ops;
	if (NULL == hw) {
		rsc->ctrl_blk = NULL;
		return 0;
	}

	switch (type) {
	case SRC:
		err = ((hw_t*)hw)->src_rsc_get_ctrl_blk(&rsc->ctrl_blk);
		break;
	case AMIXER:
		err = ((hw_t*)hw)->amixer_rsc_get_ctrl_blk(&rsc->ctrl_blk);
		break;
	case SRCIMP:
	case SUM:
	case DAIO:
		break;
	default:
		CTDPF("%s(%d) Invalid resource type value %d!\n", __func__, __LINE__, type);
		return -EINVAL;
	}

	if (err) {
		CTDPF("%s(%d) Failed to get resource control block!\n", __func__, __LINE__);
		return err;
	}

	return 0;
}

int rsc_uninit(rsc_t *rsc)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	if ((NULL != rsc->hw) && (NULL != rsc->ctrl_blk)) {
		switch (rsc->type) {
		case SRC:
			((hw_t*)rsc->hw)->src_rsc_put_ctrl_blk(rsc->ctrl_blk);
			break;
		case AMIXER:
			((hw_t*)rsc->hw)->amixer_rsc_put_ctrl_blk(rsc->ctrl_blk);
			break;
		case SUM:
		case DAIO:
			break;
		default:
			CTDPF("%s(%d) Invalid resource type value %d!\n", __func__, __LINE__, rsc->type);
			break;
		}
		
		rsc->hw = rsc->ctrl_blk = NULL;
	}

	rsc->idx = rsc->conj = 0;
	rsc->type = NUM_RSCTYP;
	rsc->msr = 0;

	return 0;
}

int rsc_mgr_init(rsc_mgr_t *mgr, RSCTYP type, unsigned int amount, void *hw)
{
	int err = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	mgr->type = NUM_RSCTYP;

	mgr->rscs = kzalloc(((amount + 8 - 1) / 8), GFP_KERNEL);
	if (NULL == mgr->rscs) {
		CTDPF("%s(%d) Memory allocation for resource manager failed!\n", __func__, __LINE__);
		return -ENOMEM;
	}

	switch (type) {
	case SRC:
		err = ((hw_t*)hw)->src_mgr_get_ctrl_blk(&mgr->ctrl_blk);
		break;
	case SRCIMP:
		err = ((hw_t*)hw)->srcimp_mgr_get_ctrl_blk(&mgr->ctrl_blk);
		break;
	case AMIXER:
		err = ((hw_t*)hw)->amixer_mgr_get_ctrl_blk(&mgr->ctrl_blk);
		break;
	case DAIO:
		err = ((hw_t*)hw)->daio_mgr_get_ctrl_blk(&mgr->ctrl_blk);
		break;
	case SUM:
		break;
	default:
		CTDPF("%s(%d) Invalid resource type value %d!\n", __func__, __LINE__, type);
		err = -EINVAL;
		goto error;
	}

	if (err) {
		CTDPF("%s(%d) Failed to get manager control block!\n", __func__, __LINE__);
		goto error;
	}

	mgr->type = type;
	mgr->avail = mgr->amount = amount;
	mgr->hw = hw;

	return 0;

error:
	kfree(mgr->rscs);
	return err;
}

int rsc_mgr_uninit(rsc_mgr_t *mgr)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	if (NULL != mgr->rscs) {
		kfree(mgr->rscs);
		mgr->rscs = NULL;
	}

	if ((NULL != mgr->hw) && (NULL != mgr->ctrl_blk)) {
		switch (mgr->type) {
		case SRC:
			((hw_t*)mgr->hw)->src_mgr_put_ctrl_blk(mgr->ctrl_blk);
			break;
		case SRCIMP:
			((hw_t*)mgr->hw)->srcimp_mgr_put_ctrl_blk(mgr->ctrl_blk);
			break;
		case AMIXER:
			((hw_t*)mgr->hw)->amixer_mgr_put_ctrl_blk(mgr->ctrl_blk);
			break;
		case DAIO:
			((hw_t*)mgr->hw)->daio_mgr_put_ctrl_blk(mgr->ctrl_blk);
			break;
		case SUM:
			break;
		default:
			CTDPF("%s(%d) Invalid resource type value %d!\n", __func__, __LINE__, mgr->type);
			break;
		}
		
		mgr->hw = mgr->ctrl_blk = NULL;
	}

	mgr->type = NUM_RSCTYP;
	mgr->avail = mgr->amount = 0;

	return 0;
}
