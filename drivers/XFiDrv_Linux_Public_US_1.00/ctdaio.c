/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	ctdaio.c
 * 
 * @Brief   
 * This file contains the implementation of Digital Audio Input Output 
 * resource management object.
 *
 * @Author	Liu Chun
 * @Date 	May 23 2008
 * 
 */

#include "ctdaio.h"
#include "cthardware.h"
#include "ctutils.h"
#include "ctimap.h"
#include <linux/slab.h>
#include <linux/kernel.h>

#define DAIO_RESOURCE_NUM	NUM_DAIOTYP
#define DAIO_OUT_MAX		SPDIFOO

typedef union daio_usage {
	struct {
		unsigned short lineo1	: 1;
		unsigned short lineo2	: 1;
		unsigned short lineo3	: 1;
		unsigned short lineo4	: 1;
		unsigned short spdifoo	: 1;
		unsigned short lineim	: 1;
		unsigned short spdifio	: 1;
		unsigned short spdifi1	: 1;
	} bf;
	unsigned short data;
} daio_usage_t;

struct daio_rsc_idx {
	unsigned int left	: 16;
	unsigned int right	: 16;
};

struct daio_rsc_idx idx_20k1[NUM_DAIOTYP] = {
	[LINEO1] = {.left = 0x00, .right = 0x01},
	[LINEO2] = {.left = 0x18, .right = 0x19},
	[LINEO3] = {.left = 0x08, .right = 0x09},
	[LINEO4] = {.left = 0x10, .right = 0x11},
	[LINEIM] = {.left = 0x1b5, .right = 0x1bd},
	[SPDIFOO] = {.left = 0x20, .right = 0x21},
	[SPDIFIO] = {.left = 0x15, .right = 0x1d},
	[SPDIFI1] = {.left = 0x95, .right = 0x9d},
};

struct daio_rsc_idx idx_20k2[NUM_DAIOTYP] = {
	[LINEO1] = {.left = 0x40, .right = 0x41},
	[LINEO2] = {.left = 0x70, .right = 0x71},
	[LINEO3] = {.left = 0x50, .right = 0x51},
	[LINEO4] = {.left = 0x60, .right = 0x61},
	[LINEIM] = {.left = 0x45, .right = 0xc5},
	[SPDIFOO] = {.left = 0x00, .right = 0x01},
	[SPDIFIO] = {.left = 0x05, .right = 0x85},
};

static int daio_master(rsc_t *rsc)
{
	/* Actually, this is not the resource index of DAIO. 
	 * For DAO, it is the input mapper index. And, for DAI, 
	 * it is the output time-slot index. */
	return (rsc->conj = rsc->idx);
}

static int daio_index(const rsc_t *rsc)
{
	return rsc->conj;
}

static int daio_out_next_conj(rsc_t *rsc)
{
	return (rsc->conj += 2);
}

static int daio_in_next_conj_20k1(rsc_t *rsc)
{
	return (rsc->conj += 0x200);
}

static int daio_in_next_conj_20k2(rsc_t *rsc)
{
	return (rsc->conj += 0x100);
}

static struct rsc_ops daio_out_rsc_ops = {
	.master		= daio_master,
	.next_conj	= daio_out_next_conj,
	.index		= daio_index,
	.output_slot	= NULL,
};

static struct rsc_ops daio_in_rsc_ops_20k1 = {
	.master		= daio_master,
	.next_conj	= daio_in_next_conj_20k1,
	.index		= NULL,
	.output_slot	= daio_index,
};

static struct rsc_ops daio_in_rsc_ops_20k2 = {
	.master		= daio_master,
	.next_conj	= daio_in_next_conj_20k2,
	.index		= NULL,
	.output_slot	= daio_index,
};

static int dao_set_left_input(dao_t *dao, rsc_t *input)
{
	struct imapper *entry = NULL;
	struct daio *daio = &dao->daio;
	int i = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	entry = kzalloc((sizeof(*entry) * daio->rscl.msr), GFP_KERNEL);
	if (NULL == entry) {
		return -ENOMEM;
	}

	/* Program master and conjugate resources */
	input->ops->master(input);
	daio->rscl.ops->master(&daio->rscl);
	for (i = 0; i < daio->rscl.msr; i++, entry++) {
		entry->slot = input->ops->output_slot(input);
		entry->user = entry->addr = daio->rscl.ops->index(&daio->rscl);
		dao->mgr->imap_add(dao->mgr, entry);
		dao->imappers[i] = entry;

		input->ops->next_conj(input);
		daio->rscl.ops->next_conj(&daio->rscl);
	}
	input->ops->master(input);
	daio->rscl.ops->master(&daio->rscl);

	return 0;
}

static int dao_set_right_input(dao_t *dao, rsc_t *input)
{
	struct imapper *entry = NULL;
	struct daio *daio = &dao->daio;
	int i = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	entry = kzalloc((sizeof(*entry) * daio->rscr.msr), GFP_KERNEL);
	if (NULL == entry) {
		return -ENOMEM;
	}

	/* Program master and conjugate resources */
	input->ops->master(input);
	daio->rscr.ops->master(&daio->rscr);
	for (i = 0; i < daio->rscr.msr; i++, entry++) {
		entry->slot = input->ops->output_slot(input);
		entry->user = entry->addr = daio->rscr.ops->index(&daio->rscr);
		dao->mgr->imap_add(dao->mgr, entry);
		dao->imappers[daio->rscl.msr + i] = entry;

		input->ops->next_conj(input);
		daio->rscr.ops->next_conj(&daio->rscr);
	}
	input->ops->master(input);
	daio->rscr.ops->master(&daio->rscr);

	return 0;
}

static int dao_clear_left_input(dao_t *dao)
{
	struct imapper *entry = NULL;
	struct daio *daio = &dao->daio;
	int i = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != dao->imappers[0]);

	entry = dao->imappers[0];
	dao->mgr->imap_delete(dao->mgr, entry);
	/* Program conjugate resources */
	for (i = 1; i < daio->rscl.msr; i++) {
		entry = dao->imappers[i];
		dao->mgr->imap_delete(dao->mgr, entry);
		dao->imappers[i] = NULL;
	}

	kfree(dao->imappers[0]);
	dao->imappers[0] = NULL;

	return 0;
}

static int dao_clear_right_input(dao_t *dao)
{
	struct imapper *entry = NULL;
	struct daio *daio = &dao->daio;
	int i = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != dao->imappers[daio->rscl.msr]);

	entry = dao->imappers[daio->rscl.msr];
	dao->mgr->imap_delete(dao->mgr, entry);
	/* Program conjugate resources */
	for (i = 1; i < daio->rscr.msr; i++) {
		entry = dao->imappers[daio->rscl.msr + i];
		dao->mgr->imap_delete(dao->mgr, entry);
		dao->imappers[daio->rscl.msr + i] = NULL;
	}

	kfree(dao->imappers[daio->rscl.msr]);
	dao->imappers[daio->rscl.msr] = NULL;

	return 0;
}

static struct dao_rsc_ops dao_ops = {
	.set_left_input		= dao_set_left_input,
	.set_right_input	= dao_set_right_input,
	.clear_left_input	= dao_clear_left_input,
	.clear_right_input	= dao_clear_right_input,
};

static unsigned int daio_device_index(DAIOTYP type, struct hw *hw)
{
	switch (hw->get_chip_type(hw)) {
	case ATC20K1:
		switch (type) {
		case SPDIFOO:	return 0;
		case SPDIFIO:	return 0;
		case SPDIFI1:	return 1;
		case LINEO1:	return 4;
		case LINEO2:	return 7;
		case LINEO3:	return 5;
		case LINEO4:	return 6;
		case LINEIM:	return 7;
		default:	return -EINVAL;
		}
	case ATC20K2:
		switch (type) {
		case SPDIFOO:	return 0;
		case SPDIFIO:	return 0;
		case LINEO1:	return 4;
		case LINEO2:	return 7;
		case LINEO3:	return 5;
		case LINEO4:	return 6;
		case LINEIM:	return 4;
		default:	return -EINVAL;
		}
	default:
		return -EINVAL;
	}
}

static int dai_set_srt_srcl(dai_t *dai, rsc_t *src)
{
	src->ops->master(src);
	((hw_t*)dai->hw)->dai_srt_set_srcm(dai->ctrl_blk, src->ops->index(src));
	return 0;
}

static int dai_set_srt_srcr(dai_t *dai, rsc_t *src)
{
	src->ops->master(src);
	((hw_t*)dai->hw)->dai_srt_set_srco(dai->ctrl_blk, src->ops->index(src));
	return 0;
}

static int dai_set_srt_msr(dai_t *dai, unsigned int msr)
{
	unsigned int rsr = 0;

	for (rsr = 0; msr > 1; msr >>= 1) {
		rsr++;
	}
	((hw_t*)dai->hw)->dai_srt_set_rsr(dai->ctrl_blk, rsr);
	return 0;
}

static int dai_set_enb_src(dai_t *dai, unsigned int enb)
{
	((hw_t*)dai->hw)->dai_srt_set_ec(dai->ctrl_blk, enb);
	return 0;
}

static int dai_set_enb_srt(dai_t *dai, unsigned int enb)
{
	((hw_t*)dai->hw)->dai_srt_set_et(dai->ctrl_blk, enb);
	return 0;
}

static int dai_commit_write(dai_t *dai)
{
	((hw_t*)dai->hw)->dai_commit_write(dai->hw, 
		daio_device_index(dai->daio.type, dai->hw), dai->ctrl_blk);
	return 0;
}

static struct dai_rsc_ops dai_ops = {
	.set_srt_srcl		= dai_set_srt_srcl,
	.set_srt_srcr		= dai_set_srt_srcr,
	.set_srt_msr		= dai_set_srt_msr,
	.set_enb_src		= dai_set_enb_src,
	.set_enb_srt		= dai_set_enb_srt,
	.commit_write		= dai_commit_write,
};

static int daio_rsc_init(daio_t *daio, const daio_desc_t *desc, void *hw)
{
	int err = 0;
	unsigned int idx_l = 0, idx_r = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	switch (((hw_t*)hw)->get_chip_type(hw)) {
	case ATC20K1:
		idx_l = idx_20k1[desc->type].left;
		idx_r = idx_20k1[desc->type].right;
		break;
	case ATC20K2:
		idx_l = idx_20k2[desc->type].left;
		idx_r = idx_20k2[desc->type].right;
		break;
	default:
		return -EINVAL;
	}
	if ((err = rsc_init(&daio->rscl, idx_l, DAIO, desc->msr, hw))) {
		return err;
	}
	if ((err = rsc_init(&daio->rscr, idx_r, DAIO, desc->msr, hw))) {
		goto error1;
	}

	/* Set daio->rscl/r->ops to daio specific ones */
	if (desc->type <= DAIO_OUT_MAX) {
		daio->rscl.ops = daio->rscr.ops = &daio_out_rsc_ops;
	} else {
		switch (((hw_t*)hw)->get_chip_type(hw)) {
		case ATC20K1:
			daio->rscl.ops = daio->rscr.ops = &daio_in_rsc_ops_20k1;
			break;
		case ATC20K2:
			daio->rscl.ops = daio->rscr.ops = &daio_in_rsc_ops_20k2;
			break;
		default:
			break;
		}
	}
	daio->type = desc->type;

	return 0;

error1:
	rsc_uninit(&daio->rscl);
	return err;
}

static int daio_rsc_uninit(daio_t *daio)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	rsc_uninit(&daio->rscl);
	rsc_uninit(&daio->rscr);

	return 0;
}

static int dao_rsc_init(dao_t *dao, const daio_desc_t *desc, daio_mgr_t *mgr)
{
	int err = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	if ((err = daio_rsc_init(&dao->daio, desc, mgr->mgr.hw))) {
		return err;
	}
	dao->imappers = kzalloc(sizeof(void*)*desc->msr*2, GFP_KERNEL);
	if (NULL == dao->imappers) {
		err = -ENOMEM;
		goto error1;
	}
	dao->ops = &dao_ops;
	dao->mgr = mgr;

	return 0;

error1:
	daio_rsc_uninit(&dao->daio);
	return err;
}

static int dao_rsc_uninit(dao_t *dao)
{
	kfree(dao->imappers);
	dao->imappers = NULL;
	daio_rsc_uninit(&dao->daio);

	return 0;
}

static int dai_rsc_init(dai_t *dai, const daio_desc_t *desc, daio_mgr_t *mgr)
{
	int err = 0;
	hw_t *hw = mgr->mgr.hw;
	unsigned int rsr = 0, msr = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	if ((err = daio_rsc_init(&dai->daio, desc, mgr->mgr.hw))) {
		return err;
	}
	dai->ops = &dai_ops;
	dai->hw = mgr->mgr.hw;
	if ((err = hw->dai_get_ctrl_blk(&dai->ctrl_blk))) {
		goto error1;
	}
	
	for (rsr = 0, msr = desc->msr; msr > 1; msr >>= 1) {
		rsr++;
	}
	hw->dai_srt_set_rsr(dai->ctrl_blk, rsr);
	hw->dai_srt_set_drat(dai->ctrl_blk, 0);
	hw->dai_srt_set_ec(dai->ctrl_blk, 0); /* default to disabling control of a SRC */
	hw->dai_srt_set_et(dai->ctrl_blk, 0); /* default to disabling SRT */
	hw->dai_commit_write(hw, 
		daio_device_index(dai->daio.type, dai->hw), dai->ctrl_blk);

	return 0;

error1:
	daio_rsc_uninit(&dai->daio);
	return err;
}

static int dai_rsc_uninit(dai_t *dai)
{
	((hw_t*)dai->hw)->dai_put_ctrl_blk(dai->ctrl_blk);
	dai->hw = dai->ctrl_blk = NULL;
	daio_rsc_uninit(&dai->daio);
	return 0;
}

static int daio_mgr_get_rsc(rsc_mgr_t *mgr, DAIOTYP type)
{
	if (((daio_usage_t *)mgr->rscs)->data & (0x1 << type)) {
		return -ENOENT;
	}

	((daio_usage_t *)mgr->rscs)->data |= (0x1 << type);

	return 0;
}

static int daio_mgr_put_rsc(rsc_mgr_t *mgr, DAIOTYP type)
{
	((daio_usage_t *)mgr->rscs)->data &= ~(0x1 << type);

	return 0;
}

static int get_daio_rsc(daio_mgr_t *mgr, const daio_desc_t *desc, daio_t **rdaio)
{
	int err = 0;
	dai_t *dai = NULL;
	dao_t *dao = NULL;
	unsigned long flags;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != mgr);
	CTASSERT(NULL != rdaio);

	*rdaio = NULL;
	
	/* Check whether there are sufficient daio resources to meet request. */
	spin_lock_irqsave(&mgr->mgr_lock, flags);
	err = daio_mgr_get_rsc(&mgr->mgr, desc->type);
	spin_unlock_irqrestore(&mgr->mgr_lock, flags);
	if (err) {
		CTDPF("%s(%d) Can't meet resource request!\n", __func__, __LINE__);
		return err;
	}

	/* Allocate mem for daio resource */
	if (desc->type <= DAIO_OUT_MAX) {
		if (NULL == (dao = kzalloc(sizeof(*dao), GFP_KERNEL))) {
			CTDPF("%s(%d) Memory allocation for DAO resource failed!\n", __func__, __LINE__);
			err = -ENOMEM;
			goto error;
		}
		if ((err = dao_rsc_init(dao, desc, mgr))) {
			goto error;
		}
		*rdaio = &dao->daio;
	} else {
		if (NULL == (dai = kzalloc(sizeof(*dai), GFP_KERNEL))) {
			CTDPF("%s(%d) Memory allocation for DAI resource failed!\n", __func__, __LINE__);
			err = -ENOMEM;
			goto error;
		}
		if ((err = dai_rsc_init(dai, desc, mgr))) {
			goto error;
		}
		*rdaio = &dai->daio;
	}

	mgr->daio_enable(mgr, *rdaio);
	mgr->commit_write(mgr);

	return 0;

error:
	if (NULL != dao) {
		kfree(dao);
	} else if (NULL != dai) {
		kfree(dai);
	}
	spin_lock_irqsave(&mgr->mgr_lock, flags);
	daio_mgr_put_rsc(&mgr->mgr, desc->type);
	spin_unlock_irqrestore(&mgr->mgr_lock, flags);
	return err;
}

static int put_daio_rsc(daio_mgr_t *mgr, daio_t *daio)
{
	unsigned int flags;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	mgr->daio_disable(mgr, daio);
	mgr->commit_write(mgr);

	spin_lock_irqsave(&mgr->mgr_lock, flags);
	daio_mgr_put_rsc(&mgr->mgr, daio->type);
	spin_unlock_irqrestore(&mgr->mgr_lock, flags);

	if (daio->type <= DAIO_OUT_MAX) {
		dao_rsc_uninit(container_of(daio, struct dao, daio));
		kfree(container_of(daio, struct dao, daio));
	} else {
		dai_rsc_uninit(container_of(daio, struct dai, daio));
		kfree(container_of(daio, struct dai, daio));
	}

	return 0;
}

static int daio_mgr_enb_daio(daio_mgr_t *mgr, daio_t *daio)
{
	struct hw *hw = mgr->mgr.hw;

	if (DAIO_OUT_MAX >= daio->type) {
		hw->daio_mgr_enb_dao(mgr->mgr.ctrl_blk, 
				daio_device_index(daio->type, hw));
	} else {
		hw->daio_mgr_enb_dai(mgr->mgr.ctrl_blk, 
				daio_device_index(daio->type, hw));
	}
	return 0;
}

static int daio_mgr_dsb_daio(daio_mgr_t *mgr, daio_t *daio)
{
	struct hw *hw = mgr->mgr.hw;

	if (DAIO_OUT_MAX >= daio->type) {
		hw->daio_mgr_dsb_dao(mgr->mgr.ctrl_blk, 
				daio_device_index(daio->type, hw));
	} else {
		hw->daio_mgr_dsb_dai(mgr->mgr.ctrl_blk, 
				daio_device_index(daio->type, hw));
	}
	return 0;
}

static int daio_map_op(void *data, struct imapper *entry)
{
	struct rsc_mgr *mgr = &((struct daio_mgr *)data)->mgr;

	((hw_t*)mgr->hw)->daio_mgr_set_imaparc(mgr->ctrl_blk, entry->slot);
	((hw_t*)mgr->hw)->daio_mgr_set_imapnxt(mgr->ctrl_blk, entry->next);
	((hw_t*)mgr->hw)->daio_mgr_set_imapaddr(mgr->ctrl_blk, entry->addr);
	((hw_t*)mgr->hw)->daio_mgr_commit_write(mgr->hw, mgr->ctrl_blk);

	return 0;
}

static int daio_imap_add(daio_mgr_t *mgr, struct imapper *entry)
{
	unsigned int flags;
	int err = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != mgr);
	CTASSERT(NULL != entry);

	spin_lock_irqsave(&mgr->imap_lock, flags);
	if ((0 == entry->addr) && (mgr->init_imap_added)) {
		input_mapper_delete(&mgr->imappers, mgr->init_imap, daio_map_op, mgr);
		mgr->init_imap_added = 0;
	}
	err = input_mapper_add(&mgr->imappers, entry, daio_map_op, mgr);
	spin_unlock_irqrestore(&mgr->imap_lock, flags);

	return err;
}

static int daio_imap_delete(daio_mgr_t *mgr, struct imapper *entry)
{
	unsigned int flags;
	int err = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != mgr);
	CTASSERT(NULL != entry);

	spin_lock_irqsave(&mgr->imap_lock, flags);
	err = input_mapper_delete(&mgr->imappers, entry, daio_map_op, mgr);
	if (list_empty(&mgr->imappers)) {
		input_mapper_add(&mgr->imappers, mgr->init_imap, daio_map_op, mgr);
		mgr->init_imap_added = 1;
	}
	spin_unlock_irqrestore(&mgr->imap_lock, flags);

	return err;
}

static int daio_mgr_commit_write(struct daio_mgr *mgr)
{
	struct hw *hw = mgr->mgr.hw;

	hw->daio_mgr_commit_write(hw, mgr->mgr.ctrl_blk);
	return 0;
}

int daio_mgr_create(void *hw, daio_mgr_t **rdaio_mgr)
{
	int err = 0, i = 0;
	daio_mgr_t *daio_mgr;
	struct imapper *entry;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != hw);
	CTASSERT(NULL != rdaio_mgr);

	*rdaio_mgr = NULL;
	daio_mgr = kzalloc(sizeof(*daio_mgr), GFP_KERNEL);
	if (NULL == daio_mgr) {
		CTDPF("%s(%d) Memory allocation for DAIO manager failed!\n", __func__, __LINE__);
		return -ENOMEM;
	}
	
	if ((err = rsc_mgr_init(&daio_mgr->mgr, DAIO, DAIO_RESOURCE_NUM, hw))) {
		goto error1;
	}

	spin_lock_init(&daio_mgr->mgr_lock);
	spin_lock_init(&daio_mgr->imap_lock);
	INIT_LIST_HEAD(&daio_mgr->imappers);
	entry = kzalloc(sizeof(*entry), GFP_KERNEL);
	if (NULL == entry) {
		err = -ENOMEM;
		goto error2;
	}
	entry->slot = entry->addr = entry->next = entry->user = 0;
	list_add(&entry->list, &daio_mgr->imappers);
	daio_mgr->init_imap = entry;
	daio_mgr->init_imap_added = 1;

	daio_mgr->get_daio = get_daio_rsc;
	daio_mgr->put_daio = put_daio_rsc;
	daio_mgr->daio_enable = daio_mgr_enb_daio;
	daio_mgr->daio_disable = daio_mgr_dsb_daio;
	daio_mgr->imap_add = daio_imap_add;
	daio_mgr->imap_delete = daio_imap_delete;
	daio_mgr->commit_write = daio_mgr_commit_write;

	for (i = 0; i < 8; i++) {
		((hw_t*)hw)->daio_mgr_dsb_dao(daio_mgr->mgr.ctrl_blk, i);
		((hw_t*)hw)->daio_mgr_dsb_dai(daio_mgr->mgr.ctrl_blk, i);
	}
	((hw_t*)hw)->daio_mgr_commit_write(hw, daio_mgr->mgr.ctrl_blk);

	*rdaio_mgr = daio_mgr;

	return 0;

error2:
	rsc_mgr_uninit(&daio_mgr->mgr);
error1:
	kfree(daio_mgr);
	return err;
}

int daio_mgr_destroy(daio_mgr_t *daio_mgr)
{
	unsigned int flags;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	/* free daio input mapper list */
	spin_lock_irqsave(&daio_mgr->imap_lock, flags);
	free_input_mapper_list(&daio_mgr->imappers);
	spin_unlock_irqrestore(&daio_mgr->imap_lock, flags);

	rsc_mgr_uninit(&daio_mgr->mgr);
	kfree(daio_mgr);

	return 0;
}

