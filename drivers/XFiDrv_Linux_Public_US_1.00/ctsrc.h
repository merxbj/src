/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	ctsrc.h
 * 
 * @Brief   
 * This file contains the definition of the Sample Rate Convertor 
 * resource management object. 
 *
 * @Author	Liu Chun
 * @Date 	May 13 2008
 * 
 */

#ifndef CTSRC_H
#define CTSRC_H

#include "ctresource.h"
#include "ctimap.h"
#include <linux/spinlock.h>
#include <linux/list.h>

#define SRC_STATE_OFF	0x0
#define SRC_STATE_INIT	0x4
#define SRC_STATE_RUN	0x5

#define SRC_SF_U8	0x0
#define SRC_SF_S16	0x1
#define SRC_SF_S24	0x2
#define SRC_SF_S32	0x3
#define SRC_SF_F32	0x4

/* Define the descriptor of a src resource */
enum SRCMODE {
	MEMRD,		/* Read data from host memory */	
	MEMWR,		/* Write data to host memory */
	ARCRW,		/* Read from and write to audio ring channel */
	NUM_SRCMODES
};

struct src_rsc_ops;
typedef struct src_rsc_ops src_rsc_ops_t;

typedef struct src {
	rsc_t rsc;		/* Basic resource info */
	struct src *intlv;	/* Pointer to next interleaved SRC in a series */
	unsigned int multi : 4;	/* Number of contiguous srcs for interleaved usage */
	unsigned int mode  : 2;	/* Working mode of this SRC resource */
	src_rsc_ops_t *ops;	/* SRC specific operations */
} src_t;

struct src_rsc_ops {
	int (*set_state)(src_t *src, unsigned int state);
	int (*set_bm)(src_t *src, unsigned int bm);
	int (*set_sf)(src_t *src, unsigned int sf);
	int (*set_pm)(src_t *src, unsigned int pm);
	int (*set_rom)(src_t *src, unsigned int rom);
	int (*set_vo)(src_t *src, unsigned int vo);
	int (*set_st)(src_t *src, unsigned int st);
	int (*set_cisz)(src_t *src, unsigned int cisz);
	int (*set_ca)(src_t *src, unsigned int ca);
	int (*set_sa)(src_t *src, unsigned int sa);
	int (*set_la)(src_t *src, unsigned int la);
	int (*set_pitch)(src_t *src, unsigned int pitch);
	int (*set_clr_zbufs)(src_t *src);
	int (*commit_write)(src_t *src);
	int (*get_ca)(src_t *src);
	int (*init)(src_t *src);
	src_t* (*next_interleave)(src_t *src);
};

/* Define src resource request description info */
typedef struct src_desc {
	u32 multi : 8;	/* Number of contiguous master srcs for interleaved usage */
	u32 msr   : 4;
	u32 mode  : 2;	/* Working mode of the requested srcs */
} src_desc_t;

/* Define src manager object */
typedef struct src_mgr {
	rsc_mgr_t mgr;	/* Basic resource manager info */
	spinlock_t mgr_lock;

	 /* request src resource */
	int (*get_src)(struct src_mgr *mgr, const src_desc_t *desc, src_t **rsrc);
	/* return src resource */
	int (*put_src)(struct src_mgr *mgr, src_t *src); 
	int (*src_enable_s)(struct src_mgr *mgr, src_t *src);
	int (*src_enable)(struct src_mgr *mgr, src_t *src);
	int (*src_disable)(struct src_mgr *mgr, src_t *src);
	int (*commit_write)(struct src_mgr *mgr);
} src_mgr_t;

/* Define the descriptor of a SRC Input Mapper resource */
struct srcimp_mgr;
struct srcimp_rsc_ops;

typedef struct srcimp {
	rsc_t rsc;
	unsigned char idx[8];
	struct imapper *imappers; 
	unsigned int mapped;		/* A bit-map indicating which conj rsc is mapped */
	struct srcimp_mgr *mgr;
	struct srcimp_rsc_ops *ops;
} srcimp_t;

struct srcimp_rsc_ops {
	int (*map)(srcimp_t *srcimp, src_t *user, rsc_t *input);
	int (*unmap)(srcimp_t *srcimp);
};

/* Define SRCIMP resource request description info */
typedef struct srcimp_desc {
	unsigned int msr;
} srcimp_desc_t;

typedef struct srcimp_mgr {
	rsc_mgr_t mgr;	/* Basic resource manager info */
	spinlock_t mgr_lock;
	spinlock_t imap_lock;
	struct list_head imappers;
	struct imapper *init_imap;
	unsigned int init_imap_added;

	 /* request srcimp resource */
	int (*get_srcimp)(struct srcimp_mgr *mgr, 
			  const srcimp_desc_t *desc, srcimp_t **rsrcimp);
	/* return srcimp resource */
	int (*put_srcimp)(struct srcimp_mgr *mgr, srcimp_t *srcimp); 
	int (*imap_add)(struct srcimp_mgr *mgr, struct imapper *entry);
	int (*imap_delete)(struct srcimp_mgr *mgr, struct imapper *entry);
} srcimp_mgr_t;

/* Constructor and destructor of SRC resource manager */
int src_mgr_create(void *hw, src_mgr_t **rsrc_mgr);
int src_mgr_destroy(src_mgr_t *src_mgr);
/* Constructor and destructor of SRCIMP resource manager */
int srcimp_mgr_create(void *hw, srcimp_mgr_t **rsrc_mgr);
int srcimp_mgr_destroy(srcimp_mgr_t *srcimp_mgr);

#endif /* CTSRC_H */
