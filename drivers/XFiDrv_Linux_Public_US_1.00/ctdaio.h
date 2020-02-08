/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	ctdaio.h
 * 
 * @Brief   
 * This file contains the definition of Digital Audio Input Output 
 * resource management object.
 *
 * @Author	Liu Chun
 * @Date 	May 23 2008
 * 
 */

#ifndef CTDAIO_H
#define CTDAIO_H

#include "ctresource.h"
#include "ctimap.h"
#include <linux/spinlock.h>
#include <linux/list.h>

/* Define the descriptor of a daio resource */
typedef enum {
	LINEO1,
	LINEO2,
	LINEO3,
	LINEO4,
	SPDIFOO,	/* S/PDIF Out (Flexijack/Optical) */
	LINEIM,
	SPDIFIO,	/* S/PDIF In (Flexijack/Optical) on the card */
	SPDIFI1,	/* S/PDIF In on internal Drive Bay */
	NUM_DAIOTYP
} DAIOTYP;

struct dao_rsc_ops;
struct dai_rsc_ops;
struct daio_mgr;

typedef struct daio {
	rsc_t rscl;			/* Basic resource info for left TX/RX */
	rsc_t rscr;			/* Basic resource info for right TX/RX */
	DAIOTYP type;
} daio_t;

typedef struct dao {
	daio_t daio;
	struct dao_rsc_ops *ops;	/* DAO specific operations */
	struct imapper **imappers;
	struct daio_mgr *mgr;
} dao_t;

typedef struct dai {
	daio_t daio;
	struct dai_rsc_ops *ops;	/* DAI specific operations */
	void *hw;
	void *ctrl_blk;
} dai_t;

struct dao_rsc_ops {
	int (*set_left_input)(dao_t *dao, rsc_t *input);
	int (*set_right_input)(dao_t *dao, rsc_t *input);
	int (*clear_left_input)(dao_t *dao);
	int (*clear_right_input)(dao_t *dao);
};

struct dai_rsc_ops {
	int (*set_srt_srcl)(dai_t *dai, rsc_t *src);
	int (*set_srt_srcr)(dai_t *dai, rsc_t *src);
	int (*set_srt_msr)(dai_t *dai, unsigned int msr);
	int (*set_enb_src)(dai_t *dai, unsigned int enb);
	int (*set_enb_srt)(dai_t *dai, unsigned int enb);
	int (*commit_write)(dai_t *dai);
};

/* Define daio resource request description info */
typedef struct daio_desc {
	DAIOTYP type;
	unsigned int msr;
} daio_desc_t;

typedef struct daio_mgr {
	rsc_mgr_t mgr;	/* Basic resource manager info */
	spinlock_t mgr_lock;
	spinlock_t imap_lock;
	struct list_head imappers;
	struct imapper *init_imap;
	unsigned int init_imap_added;

	 /* request one daio resource */
	int (*get_daio)(struct daio_mgr *mgr, const daio_desc_t *desc, daio_t **rdaio);
	/* return one daio resource */
	int (*put_daio)(struct daio_mgr *mgr, daio_t *daio);
	int (*daio_enable)(struct daio_mgr *mgr, daio_t *daio);
	int (*daio_disable)(struct daio_mgr *mgr, daio_t *daio);
	int (*imap_add)(struct daio_mgr *mgr, struct imapper *entry);
	int (*imap_delete)(struct daio_mgr *mgr, struct imapper *entry);
	int (*commit_write)(struct daio_mgr *mgr);
} daio_mgr_t;

/* Constructor and destructor of daio resource manager */
int daio_mgr_create(void *hw, daio_mgr_t **rdaio_mgr);
int daio_mgr_destroy(daio_mgr_t *daio_mgr);

#endif /* CTDAIO_H */
