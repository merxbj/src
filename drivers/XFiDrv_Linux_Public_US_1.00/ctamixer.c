/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	ctamixer.c
 * 
 * @Brief   
 * This file contains the implementation of the Audio Mixer 
 * resource management object. 
 *
 * @Author	Liu Chun
 * @Date 	May 21 2008
 * 
 */

#include "ctamixer.h"
#include "cthardware.h"
#include "ctutils.h"
#include <linux/slab.h>

#define AMIXER_RESOURCE_NUM	256
#define SUM_RESOURCE_NUM	256

#define AMIXER_Y_IMMEDIATE	1

#define BLANK_SLOT		4094

static int amixer_master(rsc_t *rsc)
{
	rsc->conj = 0;
	return (rsc->idx = container_of(rsc, struct amixer, rsc)->idx[0]);
}

static int amixer_next_conj(rsc_t *rsc)
{
	rsc->conj++;
	return container_of(rsc, struct amixer, rsc)->idx[rsc->conj];
}

static int amixer_index(const rsc_t *rsc)
{
	return container_of(rsc, struct amixer, rsc)->idx[rsc->conj];
}

static int amixer_output_slot(const rsc_t *rsc)
{
	return ((amixer_index(rsc) << 4) + 0x4);
}

static struct rsc_ops amixer_basic_rsc_ops = {
	.master		= amixer_master,
	.next_conj	= amixer_next_conj,
	.index		= amixer_index,
	.output_slot	= amixer_output_slot,
};

static int amixer_set_input(amixer_t *amixer, rsc_t *rsc)
{
	hw_t *hw = NULL;
	
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != amixer);

	hw = (hw_t*)amixer->rsc.hw;
	CTASSERT(NULL != hw);

	hw->amixer_set_mode(amixer->rsc.ctrl_blk, AMIXER_Y_IMMEDIATE);
	amixer->input = rsc;
	if (NULL == rsc) {
		hw->amixer_set_x(amixer->rsc.ctrl_blk, BLANK_SLOT);
	} else {
		hw->amixer_set_x(amixer->rsc.ctrl_blk, rsc->ops->output_slot(rsc));
	}

	return 0;
}

/* y is a 14-bit immediate constant */
static int amixer_set_y(amixer_t *amixer, unsigned int y)
{
	hw_t *hw = NULL;
	
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != amixer);
	/*CTASSERT(y < 0x10000000);*/

	hw = (hw_t*)amixer->rsc.hw;
	CTASSERT(NULL != hw);

	hw->amixer_set_y(amixer->rsc.ctrl_blk, y);

	return 0;
}

static int amixer_set_invalid_squash(amixer_t *amixer, unsigned int iv)
{
	hw_t *hw = NULL;
	
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != amixer);
	/*CTASSERT(y < 0x10000000);*/

	hw = (hw_t*)amixer->rsc.hw;
	CTASSERT(NULL != hw);

	hw->amixer_set_iv(amixer->rsc.ctrl_blk, iv);

	return 0;
}

static int amixer_set_sum(amixer_t *amixer, sum_t *sum)
{
	hw_t *hw = NULL;
	
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != amixer);

	hw = (hw_t*)amixer->rsc.hw;
	CTASSERT(NULL != hw);

	amixer->sum = sum;
	if (NULL == sum) {
		hw->amixer_set_se(amixer->rsc.ctrl_blk, 0);
	} else {
		hw->amixer_set_se(amixer->rsc.ctrl_blk, 1);
		hw->amixer_set_sadr(amixer->rsc.ctrl_blk, 
					sum->rsc.ops->index(&sum->rsc));
	}

	return 0;
}

static int amixer_commit_write(amixer_t *amixer)
{
	hw_t *hw = NULL;
	unsigned int index = 0;
	int i = 0;
	struct rsc *input = NULL;
	struct sum *sum = NULL;
	
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != amixer);

	hw = (hw_t*)amixer->rsc.hw;
	CTASSERT(NULL != hw);
	input = amixer->input;
	sum = amixer->sum;

	/* Program master and conjugate resources */
	amixer->rsc.ops->master(&amixer->rsc);
	if (NULL != input) {
		input->ops->master(input);
	}
	if (NULL != sum) {
		sum->rsc.ops->master(&sum->rsc);
	}
	for (i = 0; i < amixer->rsc.msr; i++) {
		hw->amixer_set_dirty_all(amixer->rsc.ctrl_blk);
		if (NULL != input) {
			hw->amixer_set_x(amixer->rsc.ctrl_blk, 
						input->ops->output_slot(input));
			input->ops->next_conj(input);
		}
		if (NULL != sum) {
			hw->amixer_set_sadr(amixer->rsc.ctrl_blk, 
						sum->rsc.ops->index(&sum->rsc));
			sum->rsc.ops->next_conj(&sum->rsc);
		}
		index = amixer->rsc.ops->output_slot(&amixer->rsc);
		hw->amixer_commit_write(hw, index, amixer->rsc.ctrl_blk);
		amixer->rsc.ops->next_conj(&amixer->rsc);
	}
	amixer->rsc.ops->master(&amixer->rsc);
	if (NULL != input) {
		input->ops->master(input);
	}
	if (NULL != sum) {
		sum->rsc.ops->master(&sum->rsc);
	}

	return 0;
}

static int amixer_commit_raw_write(amixer_t *amixer)
{
	hw_t *hw = NULL;
	unsigned int index = 0;
	
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != amixer);

	hw = (hw_t*)amixer->rsc.hw;
	CTASSERT(NULL != hw);

	index = amixer->rsc.ops->output_slot(&amixer->rsc);
	hw->amixer_commit_write(hw, index, amixer->rsc.ctrl_blk);

	return 0;
}

static int amixer_get_y(amixer_t *amixer)
{
	hw_t *hw = NULL;
	
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != amixer);

	hw = (hw_t*)amixer->rsc.hw;
	CTASSERT(NULL != hw);

	return hw->amixer_get_y(amixer->rsc.ctrl_blk);
}

static int amixer_setup(amixer_t *amixer, rsc_t *input, unsigned int scale, sum_t *sum)
{
	amixer_set_input(amixer, input);
	amixer_set_y(amixer, scale);
	amixer_set_sum(amixer, sum);
	amixer_commit_write(amixer);
	return 0;
}

static amixer_rsc_ops_t amixer_ops = {
	.set_input		= amixer_set_input,
	.set_invalid_squash	= amixer_set_invalid_squash,
	.set_scale		= amixer_set_y,
	.set_sum		= amixer_set_sum,
	.commit_write		= amixer_commit_write,
	.commit_raw_write	= amixer_commit_raw_write,
	.setup			= amixer_setup,
	.get_scale		= amixer_get_y,
};

static int 
amixer_rsc_init(amixer_t *amixer, const amixer_desc_t *desc, amixer_mgr_t *mgr)
{
	int err = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	err = rsc_init(&amixer->rsc, amixer->idx[0], AMIXER, desc->msr, mgr->mgr.hw);
	if (err) {
		return err;
	}

	/* Set amixer specific operations */
	amixer->rsc.ops = &amixer_basic_rsc_ops;
	amixer->ops = &amixer_ops;
	amixer->input = NULL;
	amixer->sum = NULL;

	amixer_setup(amixer, NULL, 0, NULL);

	return 0;
}

static int amixer_rsc_uninit(amixer_t *amixer)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	amixer_setup(amixer, NULL, 0, NULL);
	rsc_uninit(&amixer->rsc);
	amixer->ops = NULL;
	amixer->input = NULL;
	amixer->sum = NULL;
	return 0;
}

static int get_amixer_rsc(amixer_mgr_t *mgr, const amixer_desc_t *desc, amixer_t **ramixer)
{
	int err = 0, i = 0;
	unsigned int idx = 0;
 	amixer_t *amixer = NULL;
	unsigned long flags;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != mgr);
	CTASSERT(NULL != ramixer);

	*ramixer = NULL;
	
	/* Allocate mem for amixer resource */
	amixer = kzalloc(sizeof(*amixer), GFP_KERNEL);
	if (NULL == amixer) {
		CTDPF("%s(%d) Memory allocation for AMIXER resource failed!\n", __func__, __LINE__);
		err = -ENOMEM;
		return err;
	}

	/* Check whether there are sufficient amixer resources to meet request. */
	spin_lock_irqsave(&mgr->mgr_lock, flags);
	for (i = 0; i < desc->msr; i++) {
		if ((err = mgr_get_resource(&mgr->mgr, 1, &idx))) {
			break;
		}
		amixer->idx[i] = idx;
	}
	spin_unlock_irqrestore(&mgr->mgr_lock, flags);
	if (err) {
		CTDPF("%s(%d) Can't meet resource request!\n", __func__, __LINE__);
		goto error;
	}

	if ((err = amixer_rsc_init(amixer, desc, mgr))) {
		goto error;
	}

	*ramixer = amixer;

	return 0;

error:
	spin_lock_irqsave(&mgr->mgr_lock, flags);
	for (i--; i >= 0; i--) {
		mgr_put_resource(&mgr->mgr, 1, amixer->idx[i]);
	}
	spin_unlock_irqrestore(&mgr->mgr_lock, flags);
	kfree(amixer);
	return err;
}

static int put_amixer_rsc(amixer_mgr_t *mgr, amixer_t *amixer)
{
	unsigned int flags;
	int i = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	spin_lock_irqsave(&mgr->mgr_lock, flags);
	for (i = 0; i < amixer->rsc.msr; i++) {
		mgr_put_resource(&mgr->mgr, 1, amixer->idx[i]);
	}
	spin_unlock_irqrestore(&mgr->mgr_lock, flags);
	amixer_rsc_uninit(amixer);
	kfree(amixer);

	return 0;
}

int amixer_mgr_create(void *hw, amixer_mgr_t **ramixer_mgr)
{
	int err = 0;
	amixer_mgr_t *amixer_mgr;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != hw);
	CTASSERT(NULL != ramixer_mgr);

	*ramixer_mgr = NULL;
	amixer_mgr = kzalloc(sizeof(*amixer_mgr), GFP_KERNEL);
	if (NULL == amixer_mgr) {
		CTDPF("%s(%d) Memory allocation for AMixer manager failed!\n", __func__, __LINE__);
		return -ENOMEM;
	}
	
	if ((err = rsc_mgr_init(&amixer_mgr->mgr, AMIXER, AMIXER_RESOURCE_NUM, hw))) {
		goto error;
	}

	spin_lock_init(&amixer_mgr->mgr_lock);

	amixer_mgr->get_amixer = get_amixer_rsc;
	amixer_mgr->put_amixer = put_amixer_rsc;

	*ramixer_mgr = amixer_mgr;

	return 0;

error:
	kfree(amixer_mgr);
	return err;
}

int amixer_mgr_destroy(amixer_mgr_t *amixer_mgr)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	rsc_mgr_uninit(&amixer_mgr->mgr);
	kfree(amixer_mgr);
	return 0;
}

/* SUM resource management */

static int sum_master(rsc_t *rsc)
{
	rsc->conj = 0;
	return (rsc->idx = container_of(rsc, struct sum, rsc)->idx[0]);
}	

static int sum_next_conj(rsc_t *rsc)
{
	rsc->conj++;
	return (container_of(rsc, struct sum, rsc)->idx[rsc->conj]);
}

static int sum_index(const rsc_t *rsc)
{
	return (container_of(rsc, struct sum, rsc)->idx[rsc->conj]);
}

static int sum_output_slot(const rsc_t *rsc)
{
	return ((sum_index(rsc) << 4) + 0xc);
}

static struct rsc_ops sum_basic_rsc_ops = {
	.master		= sum_master,
	.next_conj	= sum_next_conj,
	.index		= sum_index,
	.output_slot	= sum_output_slot,
};

static int sum_rsc_init(sum_t *sum, const sum_desc_t *desc, sum_mgr_t *mgr)
{
	int err = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	err = rsc_init(&sum->rsc, sum->idx[0], SUM, desc->msr, mgr->mgr.hw);
	if (err) {
		return err;
	}

	sum->rsc.ops = &sum_basic_rsc_ops;

	return 0;
}

static int sum_rsc_uninit(sum_t *sum)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	rsc_uninit(&sum->rsc);
	return 0;
}

static int get_sum_rsc(sum_mgr_t *mgr, const sum_desc_t *desc, sum_t **rsum)
{
	int err = 0, i = 0;
	unsigned int idx = 0;
 	sum_t *sum = NULL;
	unsigned long flags;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != mgr);
	CTASSERT(NULL != rsum);

	*rsum = NULL;
	
	/* Allocate mem for sum resource */
	sum = kzalloc(sizeof(*sum), GFP_KERNEL);
	if (NULL == sum) {
		CTDPF("%s(%d) Memory allocation for SUM resource failed!\n", __func__, __LINE__);
		err = -ENOMEM;
		return err;
	}

	/* Check whether there are sufficient sum resources to meet request. */
	spin_lock_irqsave(&mgr->mgr_lock, flags);
	for (i = 0; i < desc->msr; i++) {
		if ((err = mgr_get_resource(&mgr->mgr, 1, &idx))) {
			break;
		}
		sum->idx[i] = idx;
	}
	spin_unlock_irqrestore(&mgr->mgr_lock, flags);
	if (err) {
		CTDPF("%s(%d) Can't meet resource request!\n", __func__, __LINE__);
		goto error;
	}

	if ((err = sum_rsc_init(sum, desc, mgr))) {
		goto error;
	}

	*rsum = sum;

	return 0;

error:
	spin_lock_irqsave(&mgr->mgr_lock, flags);
	for (i--; i >= 0; i--) {
		mgr_put_resource(&mgr->mgr, 1, sum->idx[i]);
	}
	spin_unlock_irqrestore(&mgr->mgr_lock, flags);
	kfree(sum);
	return err;
}

static int put_sum_rsc(sum_mgr_t *mgr, sum_t *sum)
{
	unsigned int flags;
	int i = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	spin_lock_irqsave(&mgr->mgr_lock, flags);
	for (i = 0; i < sum->rsc.msr; i++) {
		mgr_put_resource(&mgr->mgr, 1, sum->idx[i]);
	}
	spin_unlock_irqrestore(&mgr->mgr_lock, flags);
	sum_rsc_uninit(sum);
	kfree(sum);

	return 0;
}

int sum_mgr_create(void *hw, sum_mgr_t **rsum_mgr)
{
	int err = 0;
	sum_mgr_t *sum_mgr;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != hw);
	CTASSERT(NULL != rsum_mgr);

	*rsum_mgr = NULL;
	sum_mgr = kzalloc(sizeof(*sum_mgr), GFP_KERNEL);
	if (NULL == sum_mgr) {
		CTDPF("%s(%d) Memory allocation for SUM manager failed!\n", __func__, __LINE__);
		return -ENOMEM;
	}
	
	if ((err = rsc_mgr_init(&sum_mgr->mgr, SUM, SUM_RESOURCE_NUM, hw))) {
		goto error;
	}

	spin_lock_init(&sum_mgr->mgr_lock);

	sum_mgr->get_sum = get_sum_rsc;
	sum_mgr->put_sum = put_sum_rsc;

	*rsum_mgr = sum_mgr;

	return 0;

error:
	kfree(sum_mgr);
	return err;
}

int sum_mgr_destroy(sum_mgr_t *sum_mgr)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	rsc_mgr_uninit(&sum_mgr->mgr);
	kfree(sum_mgr);
	return 0;
}

