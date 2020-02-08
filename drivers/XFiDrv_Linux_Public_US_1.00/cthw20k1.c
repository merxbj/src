/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	cthw20k1.c
 * 
 * @Brief   
 * This file contains the implementation of hardware access methord for 20k1.
 *
 * @Author	Liu Chun
 * @Date 	Jun 24 2008
 * 
 */

#include "cthardware.h"
#include "ctutils.h"
#include "ct20k1reg.h"
#include <linux/types.h>
#include <linux/slab.h>
#include <linux/pci.h>
#include <asm/io.h>
#include <linux/string.h>
#include <linux/spinlock.h>
#include <linux/kernel.h>
#include <linux/interrupt.h>


struct hw20k1 {
	struct hw hw;
	spinlock_t reg_20k1_lock;
	spinlock_t reg_pci_lock;
};

static u32 hw_read_20kx(struct hw *hw, u32 reg);	
static void hw_write_20kx(struct hw *hw, u32 reg, u32 data);
static u32 hw_read_pci(struct hw *hw, u32 reg);	
static void hw_write_pci(struct hw *hw, u32 reg, u32 data);

/*
 * Type definition block. 
 * The layout of control structures can be directly applied on 20k2 chip. 
 */

/* 
 * SRC control block definitions. 
 */

/* SRC resource control block */
typedef union {
	struct {
		u32 state	: 3;  /* (0:2) */
		u32 bm		: 1;  /* (3:3) */
		u32 rsr		: 2;  /* (4:5) */
		u32 sf		: 3;  /* (6:8) */
		u32 wr		: 1;  /* (9:9) */
		u32 pm		: 1;  /* (10:10) */
		u32 rom		: 2;  /* (11:12) */
		u32 vo		: 1;  /* (13:13) */
		u32 st		: 1;  /* (14:14) */
		u32 ie		: 1;  /* (15:15) */
		u32 ilsz	: 4;  /* (16:19) */
		u32 bp		: 1;  /* (20:20) */
		u32 rsv		: 11; /* (21:31) */
	} bf;
	u32 data;
} srcctl_t;

typedef union {
	struct {
		u32 cisz	: 11; /* (0:10) */
		u32 cwa		: 10; /* (11:20) */
		u32 d		: 1;  /* (21:21) */
		u32 rs		: 3;  /* (22:24) */
		u32 nal		: 5;  /* (25:29) */
		u32 ra		: 2;  /* (30:31) */
	} bf;
	u32 data;
} srcccr_t;

typedef union {
	struct {
		u32 ca		: 26; /* (0:25) */
		u32 rs		: 3;  /* (26:28) */
		u32 nal		: 3;  /* (29:31) */
	} bf;
	u32 data;
} srcca_t;

typedef union {
	struct {
		u32 sa		: 26; /* (0:25) */
		u32 rsv		: 6;  /* (26:31) */
	} bf;
	u32 data;
} srcsa_t;

typedef union {
	struct {
		u32 la		: 26; /* (0:25) */
		u32 rsv		: 6;  /* (26:31) */
	} bf;
	u32 data;
} srcla_t;

/* Mixer Parameter Ring ram Low and Hight register */
typedef struct {
	u32 data;	/* Fixed-point value in 8.24 format for parameter channel */
} mprlh_t;	

/* SRC resource register dirty flags */
typedef union src_dirty {
	struct {
		u16 ctl		: 1;
		u16 ccr		: 1;
		u16 sa		: 1;
		u16 la		: 1;
		u16 ca		: 1;
		u16 mpr		: 1;
		u16 czbfs	: 1;	/* Clear Z-Buffers */
		u16 rsv		: 9;
	} bf;
	u16 data;
} src_dirty_t;

typedef struct src_rsc_ctrl_blk {
	srcctl_t	ctl;
	srcccr_t 	ccr;
	srcca_t		ca;
	srcsa_t		sa;
	srcla_t		la;
	mprlh_t		mpr;
	src_dirty_t	dirty;
} src_rsc_ctrl_blk_t;

/* SRC manager control block */
typedef union {
	u32 data;
} srcenbsa_t;

typedef union {
	u32 data;
} srcenb_t;

typedef union {
	struct {
		u16 enb0	: 1;
		u16 enb1	: 1;
		u16 enb2	: 1;
		u16 enb3	: 1;
		u16 enb4	: 1;
		u16 enb5	: 1;
		u16 enb6	: 1;
		u16 enb7	: 1;
		u16 enbsa	: 1;
		u16 rsv		: 7;
	} bf;
	u16 data;
} src_mgr_dirty_t;

typedef struct src_mgr_ctrl_blk {
	srcenbsa_t	enbsa;
	srcenb_t	enb[8];
	src_mgr_dirty_t	dirty;
} src_mgr_ctrl_blk_t;

/* SRCIMP manager control block */
typedef union {
	struct {
		u32 arc		: 12; /* (0:11) */
		u32 rsv		: 4;  /* (12:15) */
		u32 nxt		: 8;  /* (16:23) */
		u32 src		: 8;  /* (24:31) */
	} bf;
	u32 data;
} srcaim_t;

typedef struct {
	srcaim_t srcaim;
	unsigned int idx;
} srcimap_t;

/* SRCIMP manager register dirty flags */
typedef union {
	struct {
		u16 srcimap	: 1;
		u16 rsv		: 15;
	} bf;
	u16 data;
} srcimp_mgr_dirty_t;

typedef struct srcimp_mgr_ctrl_blk {
	srcimap_t		srcimap;
	srcimp_mgr_dirty_t	dirty;
} srcimp_mgr_ctrl_blk_t;

/*
 * Function implementation block.
 */

static int src_get_rsc_ctrl_blk(void **rblk)
{
	src_rsc_ctrl_blk_t *blk;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != rblk);
	*rblk = NULL;
	blk = kzalloc(sizeof(*blk), GFP_KERNEL);
	if (NULL == blk) {
		CTDPF("%s(%d): Memory allocation failed!!!\n", __func__, __LINE__);
		return -ENOMEM;
	}

	*rblk = blk;

	return 0;
}

static int src_put_rsc_ctrl_blk(void *blk)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != blk);

	kfree((src_rsc_ctrl_blk_t*)blk);

	return 0;
}

static int src_set_state(void *blk, unsigned int state)
{
	((src_rsc_ctrl_blk_t*)blk)->ctl.bf.state = state;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.ctl = 1;
	return 0;
}

static int src_set_bm(void *blk, unsigned int bm)
{
	((src_rsc_ctrl_blk_t*)blk)->ctl.bf.bm = bm;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.ctl = 1;
	return 0;
}

static int src_set_rsr(void *blk, unsigned int rsr)
{
	((src_rsc_ctrl_blk_t*)blk)->ctl.bf.rsr = rsr;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.ctl = 1;
	return 0;
}

static int src_set_sf(void *blk, unsigned int sf)
{
	((src_rsc_ctrl_blk_t*)blk)->ctl.bf.sf = sf;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.ctl = 1;
	return 0;
}

static int src_set_wr(void *blk, unsigned int wr)
{
	((src_rsc_ctrl_blk_t*)blk)->ctl.bf.wr = wr;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.ctl = 1;
	return 0;
}

static int src_set_pm(void *blk, unsigned int pm)
{
	((src_rsc_ctrl_blk_t*)blk)->ctl.bf.pm = pm;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.ctl = 1;
	return 0;
}

static int src_set_rom(void *blk, unsigned int rom)
{
	((src_rsc_ctrl_blk_t*)blk)->ctl.bf.rom = rom;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.ctl = 1;
	return 0;
}

static int src_set_vo(void *blk, unsigned int vo)
{
	((src_rsc_ctrl_blk_t*)blk)->ctl.bf.vo = vo;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.ctl = 1;
	return 0;
}

static int src_set_st(void *blk, unsigned int st)
{
	((src_rsc_ctrl_blk_t*)blk)->ctl.bf.st = st;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.ctl = 1;
	return 0;
}

static int src_set_ie(void *blk, unsigned int ie)
{
	((src_rsc_ctrl_blk_t*)blk)->ctl.bf.ie = ie;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.ctl = 1;
	return 0;
}

static int src_set_ilsz(void *blk, unsigned int ilsz)
{
	((src_rsc_ctrl_blk_t*)blk)->ctl.bf.ilsz = ilsz;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.ctl = 1;
	return 0;
}

static int src_set_bp(void *blk, unsigned int bp)
{
	((src_rsc_ctrl_blk_t*)blk)->ctl.bf.bp = bp;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.ctl = 1;
	return 0;
}

static int src_set_cisz(void *blk, unsigned int cisz)
{
	((src_rsc_ctrl_blk_t*)blk)->ccr.bf.cisz = cisz;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.ccr = 1;
	return 0;
}

static int src_set_ca(void *blk, unsigned int ca)
{
	((src_rsc_ctrl_blk_t*)blk)->ca.bf.ca = ca;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.ca = 1;
	return 0;
}

static int src_set_sa(void *blk, unsigned int sa)
{
	((src_rsc_ctrl_blk_t*)blk)->sa.bf.sa = sa;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.sa = 1;
	return 0;
}

static int src_set_la(void *blk, unsigned int la)
{
	((src_rsc_ctrl_blk_t*)blk)->la.bf.la = la;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.la = 1;
	return 0;
}

static int src_set_pitch(void *blk, unsigned int pitch)
{
	((src_rsc_ctrl_blk_t*)blk)->mpr.data = pitch;
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.mpr = 1;
	return 0;
}

static int src_set_clear_zbufs(void *blk, unsigned int clear)
{
	((src_rsc_ctrl_blk_t*)blk)->dirty.bf.czbfs = (clear ? 1 : 0);
	return 0;
}

static int src_set_dirty(void *blk, unsigned int flags)
{
	((src_rsc_ctrl_blk_t*)blk)->dirty.data = (flags & 0xffff);
	return 0;
}

static int src_set_dirty_all(void *blk)
{
	((src_rsc_ctrl_blk_t*)blk)->dirty.data = ~(0x0);
	return 0;
}

#define AR_SLOT_SIZE		4096
#define AR_SLOT_BLOCK_SIZE	16
#define AR_PTS_PITCH		6
#define AR_PARAM_SRC_OFFSET	0x60

static unsigned int src_param_pitch_mixer(unsigned int src_idx)
{
	return ((src_idx << 4) + AR_PTS_PITCH + AR_SLOT_SIZE 
			- AR_PARAM_SRC_OFFSET) % AR_SLOT_SIZE;

}

static int src_commit_write(struct hw *hw, unsigned int idx, void *blk)
{
	src_rsc_ctrl_blk_t *ctl = (src_rsc_ctrl_blk_t*)blk;
	int i = 0;

	CTDPF("%s(%d) is called idx = 0x%x\n", __func__, __LINE__, idx);

	if (ctl->dirty.bf.czbfs) {
		/* Clear Z-Buffer registers */
		CTDPF("%s(%d) Clear Z-Buffers for SRC 0x%x\n", __func__, __LINE__, idx);
		for (i = 0; i < 8; i++) {
			hw_write_20kx(hw, SRCUPZ+idx*0x100+i*0x4, 0);
		}
		for (i = 0; i < 4; i++) {
			hw_write_20kx(hw, SRCDN0Z+idx*0x100+i*0x4, 0);
		}
		for (i = 0; i < 8; i++) {
			hw_write_20kx(hw, SRCDN1Z+idx*0x100+i*0x4, 0);
		}
		ctl->dirty.bf.czbfs = 0;
	}
	if (ctl->dirty.bf.mpr) {
		/* Take the parameter mixer resource in the same group as that
		 * the idx src is in for simplicity. Unlike src, all conjugate 
		 * parameter mixer resources must be programmed for corresponding 
		 * conjugate src resources. */
		unsigned int pm_idx = src_param_pitch_mixer(idx);
		CTDPF("%s(%d) Writing mprlh = 0x%x to pm_idx 0x%x\n", __func__, __LINE__, ctl->mpr.data, pm_idx);
		hw_write_20kx(hw, PRING_LO_HI+4*pm_idx, ctl->mpr.data);
		hw_write_20kx(hw, PMOPLO+8*pm_idx, 0x3);
		hw_write_20kx(hw, PMOPHI+8*pm_idx, 0x0);
		ctl->dirty.bf.mpr = 0;
	}
	if (ctl->dirty.bf.sa) {
		CTDPF("%s(%d) Writing srcsa = 0x%x\n", __func__, __LINE__, ctl->sa.data);
		hw_write_20kx(hw, SRCSA+idx*0x100, ctl->sa.data);
		ctl->dirty.bf.sa = 0;
	}
	if (ctl->dirty.bf.la) {
		CTDPF("%s(%d) Writing srcla = 0x%x\n", __func__, __LINE__, ctl->la.data);
		hw_write_20kx(hw, SRCLA+idx*0x100, ctl->la.data); 
		ctl->dirty.bf.la = 0;
	}
	if (ctl->dirty.bf.ca) {
		CTDPF("%s(%d) Writing srcca = 0x%x\n", __func__, __LINE__, ctl->ca.data);
		hw_write_20kx(hw, SRCCA+idx*0x100, ctl->ca.data);
		ctl->dirty.bf.ca = 0;
	}

	/* Write srccf register */
	hw_write_20kx(hw, SRCCF+idx*0x100, 0x0);

	if (ctl->dirty.bf.ccr) {
		CTDPF("%s(%d) Writing srcccr = 0x%x\n", __func__, __LINE__, ctl->ccr.data);
		hw_write_20kx(hw, SRCCCR+idx*0x100, ctl->ccr.data);
		ctl->dirty.bf.ccr = 0;
	}
	if (ctl->dirty.bf.ctl) {
		CTDPF("%s(%d) Writing srcctl = 0x%x\n", __func__, __LINE__, ctl->ctl.data);
		hw_write_20kx(hw, SRCCTL+idx*0x100, ctl->ctl.data);
		ctl->dirty.bf.ctl = 0;
	}

	return 0;
}

static int src_get_ca(struct hw *hw, unsigned int idx, void *blk)
{
	src_rsc_ctrl_blk_t *ctl = (src_rsc_ctrl_blk_t*)blk;

	/*CTDPF("%s(%d) Reading srcca = 0x%x\n", __func__, __LINE__, ctl->ca.data);*/
	ctl->ca.data = hw_read_20kx(hw, SRCCA+idx*0x100);
	ctl->dirty.bf.ca = 0;

	return ctl->ca.bf.ca;
}

static unsigned int src_get_dirty(void *blk)
{
	return ((src_rsc_ctrl_blk_t*)blk)->dirty.data;
}

static unsigned int src_dirty_conj_mask(void)
{
	return 0x20;
}

static int src_mgr_enbs_src(void *blk, unsigned int idx)
{
	/*((src_mgr_ctrl_blk_t*)blk)->enbsa.data |= (0x1 << ((idx%128)/4));*/
	((src_mgr_ctrl_blk_t*)blk)->enbsa.data = ~(0x0);
	((src_mgr_ctrl_blk_t*)blk)->dirty.bf.enbsa = 1;
	((src_mgr_ctrl_blk_t*)blk)->enb[idx/32].data |= (0x1 << (idx%32));
	return 0;
}

static int src_mgr_enb_src(void *blk, unsigned int idx)
{
	((src_mgr_ctrl_blk_t*)blk)->enb[idx/32].data |= (0x1 << (idx%32));
	((src_mgr_ctrl_blk_t*)blk)->dirty.data |= (0x1 << (idx/32));
	return 0;
}

static int src_mgr_dsb_src(void *blk, unsigned int idx)
{
	((src_mgr_ctrl_blk_t*)blk)->enb[idx/32].data &= ~(0x1 << (idx%32));
	((src_mgr_ctrl_blk_t*)blk)->dirty.data |= (0x1 << (idx/32));
	return 0;
}

static int src_mgr_commit_write(struct hw *hw, void *blk)
{
	src_mgr_ctrl_blk_t *ctl = blk;
	int i = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	if (ctl->dirty.bf.enbsa) {
		while (hw_read_20kx(hw, SRCENBSTAT) & 0x1);
		CTDPF("%s(%d) Writing enbsa = 0x%x\n", __func__, __LINE__, ctl->enbsa.data);
		hw_write_20kx(hw, SRCENBS, ctl->enbsa.data);
		ctl->dirty.bf.enbsa = 0;
	}
	for (i = 0; i < 8; i++) {
		if ((ctl->dirty.data & (0x1 << i))) {
			CTDPF("%s(%d) Writing enb[%d] = 0x%x\n", __func__, __LINE__, i, ctl->enb[i].data);
			hw_write_20kx(hw, SRCENB + (i * 0x100), ctl->enb[i].data);
			ctl->dirty.data &= ~(0x1 << i);
		}
	}

	return 0;
}

static int src_mgr_get_ctrl_blk(void **rblk)
{
	src_mgr_ctrl_blk_t *blk;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != rblk);

	*rblk = NULL;
	blk = kzalloc(sizeof(*blk), GFP_KERNEL);
	if (NULL == blk) {
		CTDPF("%s(%d): Memory allocation failed!!!\n", __func__, __LINE__);
		return -ENOMEM;
	}

	*rblk = blk;

	return 0;
}

static int src_mgr_put_ctrl_blk(void *blk)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != blk);

	kfree((src_mgr_ctrl_blk_t*)blk);

	return 0;
}

static int srcimp_mgr_get_ctrl_blk(void **rblk)
{
	srcimp_mgr_ctrl_blk_t *blk;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != rblk);
	*rblk = NULL;
	blk = kzalloc(sizeof(*blk), GFP_KERNEL);
	if (NULL == blk) {
		CTDPF("%s(%d): Memory allocation failed!!!\n", __func__, __LINE__);
		return -ENOMEM;
	}

	*rblk = blk;

	return 0;
}

static int srcimp_mgr_put_ctrl_blk(void *blk)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != blk);

	kfree((srcimp_mgr_ctrl_blk_t*)blk);

	return 0;
}

static int srcimp_mgr_set_imaparc(void *blk, unsigned int slot)
{
	((srcimp_mgr_ctrl_blk_t*)blk)->srcimap.srcaim.bf.arc = slot;
	((srcimp_mgr_ctrl_blk_t*)blk)->dirty.bf.srcimap = 1;
	return 0;
}

static int srcimp_mgr_set_imapuser(void *blk, unsigned int user)
{
	((srcimp_mgr_ctrl_blk_t*)blk)->srcimap.srcaim.bf.src = user;
	((srcimp_mgr_ctrl_blk_t*)blk)->dirty.bf.srcimap = 1;
	return 0;
}

static int srcimp_mgr_set_imapnxt(void *blk, unsigned int next)
{
	((srcimp_mgr_ctrl_blk_t*)blk)->srcimap.srcaim.bf.nxt = next;
	((srcimp_mgr_ctrl_blk_t*)blk)->dirty.bf.srcimap = 1;
	return 0;
}

static int srcimp_mgr_set_imapaddr(void *blk, unsigned int addr)
{
	((srcimp_mgr_ctrl_blk_t*)blk)->srcimap.idx = addr;
	((srcimp_mgr_ctrl_blk_t*)blk)->dirty.bf.srcimap = 1;
	return 0;
}

static int srcimp_mgr_commit_write(struct hw *hw, void *blk)
{
	srcimp_mgr_ctrl_blk_t *ctl = (srcimp_mgr_ctrl_blk_t*)blk;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	if (ctl->dirty.bf.srcimap) {
		CTDPF("%s(%d) Writing srcaim(%d) = 0x%x\n", __func__, __LINE__, ctl->srcimap.idx, ctl->srcimap.srcaim.data);
		hw_write_20kx(hw, SRCIMAP+ctl->srcimap.idx*0x100, 
						ctl->srcimap.srcaim.data);
		ctl->dirty.bf.srcimap = 0;
	}

	return 0;
}

/* 
 * AMIXER control block definitions. 
 */

typedef union {
	struct {
		u32 m		: 2;  /* (0:1) */
		u32 rsv		: 2;  /* (2:3) */
		u32 x		: 14; /* (4:17) */
		u32 y		: 14; /* (18:31) */
	} bf;
	u32 data;
} amoplo_t;

typedef union {
	struct {
		u32 sadr	: 8;  /* (0:7) */
		u32 rsv		: 23; /* (8:30) */
		u32 se		: 1;  /* (31:31) */
	} bf;
	u32 data;
} amophi_t;

/* AMIXER resource register dirty flags */
typedef union {
	struct {
		u16 amoplo	: 1;
		u16 amophi	: 1;
		u16 rsv		: 14;
	} bf;
	u16 data;
} amixer_dirty_t;

/* AMIXER resource control block */
typedef struct amixer_rsc_ctrl_blk {
	amoplo_t	amoplo;
	amophi_t	amophi;
	amixer_dirty_t	dirty;
} amixer_rsc_ctrl_blk_t;

static int amixer_set_mode(void *blk, unsigned int mode)
{
	((amixer_rsc_ctrl_blk_t*)blk)->amoplo.bf.m = mode;
	((amixer_rsc_ctrl_blk_t*)blk)->dirty.bf.amoplo = 1;
	return 0;
}

static int amixer_set_iv(void *blk, unsigned int iv)
{
	/* 20k1 amixer does not have this field */
	return 0;
}

static int amixer_set_x(void *blk, unsigned int x)
{
	((amixer_rsc_ctrl_blk_t*)blk)->amoplo.bf.x = x;
	((amixer_rsc_ctrl_blk_t*)blk)->dirty.bf.amoplo = 1;
	return 0;
}

static int amixer_set_y(void *blk, unsigned int y)
{
	((amixer_rsc_ctrl_blk_t*)blk)->amoplo.bf.y = y;
	((amixer_rsc_ctrl_blk_t*)blk)->dirty.bf.amoplo = 1;
	return 0;
}

static int amixer_set_sadr(void *blk, unsigned int sadr)
{
	((amixer_rsc_ctrl_blk_t*)blk)->amophi.bf.sadr = sadr;
	((amixer_rsc_ctrl_blk_t*)blk)->dirty.bf.amophi = 1;
	return 0;
}

static int amixer_set_se(void *blk, unsigned int se)
{
	((amixer_rsc_ctrl_blk_t*)blk)->amophi.bf.se = se;
	((amixer_rsc_ctrl_blk_t*)blk)->dirty.bf.amophi = 1;
	return 0;
}

static int amixer_set_dirty(void *blk, unsigned int flags)
{
	((amixer_rsc_ctrl_blk_t*)blk)->dirty.data = (flags & 0xffff);
	return 0;
}

static int amixer_set_dirty_all(void *blk)
{
	((amixer_rsc_ctrl_blk_t*)blk)->dirty.data = ~(0x0);
	return 0;
}

static int amixer_commit_write(struct hw *hw, unsigned int idx, void *blk)
{
	amixer_rsc_ctrl_blk_t *ctl = (amixer_rsc_ctrl_blk_t*)blk;

	CTDPF("%s(%d) is called idx = 0x%x\n", __func__, __LINE__, idx);

	if (ctl->dirty.bf.amoplo || ctl->dirty.bf.amophi) {
		CTDPF("%s(%d) Writing amoplo = 0x%x\n", __func__, __LINE__, ctl->amoplo.data);
		hw_write_20kx(hw, AMOPLO+idx*8, ctl->amoplo.data); 
		ctl->dirty.bf.amoplo = 0;
		CTDPF("%s(%d) Writing amophi = 0x%x\n", __func__, __LINE__, ctl->amophi.data);
		hw_write_20kx(hw, AMOPHI+idx*8, ctl->amophi.data); 
		ctl->dirty.bf.amophi = 0;
	}

	return 0;
}

static int amixer_get_y(void *blk)
{
	return ((amixer_rsc_ctrl_blk_t*)blk)->amoplo.bf.y;
}

static unsigned int amixer_get_dirty(void *blk)
{
	return ((amixer_rsc_ctrl_blk_t*)blk)->dirty.data;
}

static int amixer_rsc_get_ctrl_blk(void **rblk)
{
	amixer_rsc_ctrl_blk_t *blk;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != rblk);
	*rblk = NULL;
	blk = kzalloc(sizeof(*blk), GFP_KERNEL);
	if (NULL == blk) {
		CTDPF("%s(%d): Memory allocation failed!!!\n", __func__, __LINE__);
		return -ENOMEM;
	}

	*rblk = blk;

	return 0;
}

static int amixer_rsc_put_ctrl_blk(void *blk)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != blk);

	kfree((amixer_rsc_ctrl_blk_t*)blk);

	return 0;
}

static int amixer_mgr_get_ctrl_blk(void **rblk)
{
	/*amixer_mgr_ctrl_blk_t *blk;*/

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != rblk);
	*rblk = NULL;
	/*blk = kzalloc(sizeof(*blk), GFP_KERNEL);
	if (NULL == blk) {
		CTDPF("%s(%d): Memory allocation failed!!!\n", __func__, __LINE__);
		return -ENOMEM;
	}

	*rblk = blk;*/

	return 0;
}

static int amixer_mgr_put_ctrl_blk(void *blk)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != blk);

	/*kfree((amixer_mgr_ctrl_blk_t*)blk);*/

	return 0;
}

/* 
 * DAIO control block definitions. 
 */

/* Receiver Sample Rate Tracker Control register */
typedef union {
	struct {
		u32 srcr	: 8;  /* (0:7) */
		u32 srcl	: 8;  /* (8:15) */
		u32 rsr		: 2;  /* (16:17) */
		u32 drat	: 2;  /* (18:19) */
		u32 rsv 	: 8;  /* (20:27) */
		u32 rle		: 1;  /* (28:28) */
		u32 rlp		: 1;  /* (29:29) */
		u32 ec		: 1;  /* (30:30) */
		u32 et		: 1;  /* (31:31) */
	} bf;
	u32 data;
} srtctl_t;

/* DAIO Receiver register dirty flags */
typedef union {
	struct {
		u16 srtctl	: 1;
		u16 rsv		: 15;
	} bf;
	u16 data;
} dai_dirty_t;

/* DAIO Receiver control block */
typedef struct dai_ctrl_blk {
	srtctl_t	srtctl;
	dai_dirty_t	dirty;
} dai_ctrl_blk_t;

/* Audio Input Mapper RAM */
typedef union {
	struct {
		u32 arc		: 12; /* (0:11) */
		u32 rsv1	: 4;  /* (12:15) */
		u32 nxt		: 7;  /* (16:22) */
		u32 rsv2	: 9;  /* (23:31) */
	} bf;
	u32 data;
} aim_t;

typedef struct {
	aim_t aim;
	unsigned int idx;
} daoimap_t;

/* I2S Transmitter/Receiver Control register */
typedef union {
	struct {
		u8 rsv1		: 2;
		u8 ea		: 1;
		u8 rsv2		: 1;
		u8 ei		: 1;
		u8 rsv3		: 3;
	} bf[4];
	u32 data;
} i2sctl_t;

/* S/PDIF Transmitter Control register */
typedef union {
	struct {
		u8 oe		: 1;
		u8 rsv		: 7;
	} bf[4];
	u32 data;
} spoctl_t;

/* S/PDIF Receiver Control register */
typedef union {
	struct {
		u8 en		: 1;
		u8 i24		: 1;
		u8 ib		: 1;
		u8 sm		: 1;
		u8 vm		: 1;
		u8 rsv		: 3;
	} bf[4];
	u32 data;
} spictl_t;

/* DAIO manager register dirty flags */
typedef union {
	struct {
		u32 i2soctl	: 4;
		u32 i2sictl	: 4;
		u32 spoctl	: 4;
		u32 spictl	: 4;
		u32 daoimap	: 1;
		u32 rsv		: 15;
	} bf;
	u32 data;
} daio_mgr_dirty_t;

/* DAIO manager control block */
typedef struct daio_mgr_ctrl_blk {
	i2sctl_t		i2sctl;
	spoctl_t		spoctl;
	spictl_t		spictl;
	daoimap_t		daoimap;
	daio_mgr_dirty_t	dirty;
} daio_mgr_ctrl_blk_t;

static int dai_srt_set_srcr(void *blk, unsigned int src)
{
	((dai_ctrl_blk_t*)blk)->srtctl.bf.srcr = src;
	((dai_ctrl_blk_t*)blk)->dirty.bf.srtctl = 1;
	return 0;
}

static int dai_srt_set_srcl(void *blk, unsigned int src)
{
	((dai_ctrl_blk_t*)blk)->srtctl.bf.srcl = src;
	((dai_ctrl_blk_t*)blk)->dirty.bf.srtctl = 1;
	return 0;
}

static int dai_srt_set_rsr(void *blk, unsigned int rsr)
{
	((dai_ctrl_blk_t*)blk)->srtctl.bf.rsr = rsr;
	((dai_ctrl_blk_t*)blk)->dirty.bf.srtctl = 1;
	return 0;
}

static int dai_srt_set_drat(void *blk, unsigned int drat)
{
	((dai_ctrl_blk_t*)blk)->srtctl.bf.drat = drat;
	((dai_ctrl_blk_t*)blk)->dirty.bf.srtctl = 1;
	return 0;
}

static int dai_srt_set_ec(void *blk, unsigned int ec)
{
	((dai_ctrl_blk_t*)blk)->srtctl.bf.ec = (ec) ? 1 : 0;
	((dai_ctrl_blk_t*)blk)->dirty.bf.srtctl = 1;
	return 0;
}

static int dai_srt_set_et(void *blk, unsigned int et)
{
	((dai_ctrl_blk_t*)blk)->srtctl.bf.et = (et) ? 1 : 0;
	((dai_ctrl_blk_t*)blk)->dirty.bf.srtctl = 1;
	return 0;
}

static int dai_commit_write(struct hw *hw, unsigned int idx, void *blk)
{
	dai_ctrl_blk_t *ctl = blk;

	CTDPF("%s(%d) is called idx = 0x%x\n", __func__, __LINE__, idx);

	if (ctl->dirty.bf.srtctl) {
		CTDPF("%s(%d) Writing RX_SRT_CTL = 0x%x\n", __func__, __LINE__, ctl->srtctl.data);
		if (idx < 4) {
			/* S/PDIF SRTs */
			hw_write_20kx(hw, SRTSCTL+0x4*idx, ctl->srtctl.data);
		} else {
			/* I2S SRT */
			hw_write_20kx(hw, SRTICTL, ctl->srtctl.data);
		}
		ctl->dirty.bf.srtctl = 0;
	}

	return 0;
}

static int dai_get_ctrl_blk(void **rblk)
{
	dai_ctrl_blk_t *blk;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != rblk);
	*rblk = NULL;
	blk = kzalloc(sizeof(*blk), GFP_KERNEL);
	if (NULL == blk) {
		CTDPF("%s(%d): Memory allocation failed!!!\n", __func__, __LINE__);
		return -ENOMEM;
	}

	*rblk = blk;

	return 0;
}

static int dai_put_ctrl_blk(void *blk)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != blk);

	kfree((dai_ctrl_blk_t*)blk);

	return 0;
}

static int daio_mgr_enb_dai(void *blk, unsigned int idx)
{
	if (idx < 4) {
		/* S/PDIF input */
		((daio_mgr_ctrl_blk_t*)blk)->spictl.bf[idx].en = 1;
		((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.spictl |= (0x1 << idx);
	} else {
		/* I2S input */
		idx %= 4;
		((daio_mgr_ctrl_blk_t*)blk)->i2sctl.bf[idx].ei = 1;
		((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.i2sictl |= (0x1 << idx);
	}
	return 0;
}

static int daio_mgr_dsb_dai(void *blk, unsigned int idx)
{
	if (idx < 4) {
		/* S/PDIF input */
		((daio_mgr_ctrl_blk_t*)blk)->spictl.bf[idx].en = 0;
		((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.spictl |= (0x1 << idx);
	} else {
		/* I2S input */
		idx %= 4;
		((daio_mgr_ctrl_blk_t*)blk)->i2sctl.bf[idx].ei = 0;
		((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.i2sictl |= (0x1 << idx);
	}
	return 0;
}

static int daio_mgr_enb_dao(void *blk, unsigned int idx)
{
	if (idx < 4) {
		/* S/PDIF output */
		((daio_mgr_ctrl_blk_t*)blk)->spoctl.bf[idx].oe = 1;
		((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.spoctl |= (0x1 << idx);
	} else {
		/* I2S output */
		idx %= 4;
		((daio_mgr_ctrl_blk_t*)blk)->i2sctl.bf[idx].ea = 1;
		((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.i2soctl |= (0x1 << idx);
	}
	return 0;
}

static int daio_mgr_dsb_dao(void *blk, unsigned int idx)
{
	if (idx < 4) {
		/* S/PDIF output */
		((daio_mgr_ctrl_blk_t*)blk)->spoctl.bf[idx].oe = 0;
		((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.spoctl |= (0x1 << idx);
	} else {
		/* I2S output */
		idx %= 4;
		((daio_mgr_ctrl_blk_t*)blk)->i2sctl.bf[idx].ea = 0;
		((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.i2soctl |= (0x1 << idx);
	}
	return 0;
}

static int daio_mgr_set_imaparc(void *blk, unsigned int slot)
{
	((daio_mgr_ctrl_blk_t*)blk)->daoimap.aim.bf.arc = slot;
	((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.daoimap = 1;
	return 0;
}

static int daio_mgr_set_imapnxt(void *blk, unsigned int next)
{
	((daio_mgr_ctrl_blk_t*)blk)->daoimap.aim.bf.nxt = next;
	((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.daoimap = 1;
	return 0;
}

static int daio_mgr_set_imapaddr(void *blk, unsigned int addr)
{
	((daio_mgr_ctrl_blk_t*)blk)->daoimap.idx = addr;
	((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.daoimap = 1;
	return 0;
}

static int daio_mgr_commit_write(struct hw *hw, void *blk)
{
	daio_mgr_ctrl_blk_t *ctl = (daio_mgr_ctrl_blk_t*)blk;
	u32 data = 0;
	int i = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	if (ctl->dirty.bf.i2sictl || ctl->dirty.bf.i2soctl) {
		data = hw_read_20kx(hw, I2SCTL);
		for (i = 0; i < 4; i++) {
			if ((ctl->dirty.bf.i2sictl & (0x1 << i))) {
				((i2sctl_t*)&data)->bf[i].ei = ctl->i2sctl.bf[i].ei;
				ctl->dirty.bf.i2sictl &= ~(0x1 << i);
			}
			if ((ctl->dirty.bf.i2soctl & (0x1 << i))) {
				((i2sctl_t*)&data)->bf[i].ea = ctl->i2sctl.bf[i].ea;
				ctl->dirty.bf.i2soctl &= ~(0x1 << i);
			}
		}
		CTDPF("%s(%d) Writing i2sctl = 0x%x\n", __func__, __LINE__, data);
		hw_write_20kx(hw, I2SCTL, data);
	}
	if (ctl->dirty.bf.spoctl) {
		data = hw_read_20kx(hw, SPOCTL);
		for (i = 0; i < 4; i++) {
			if ((ctl->dirty.bf.spoctl & (0x1 << i))) {
				((spoctl_t*)&data)->bf[i].oe = ctl->spoctl.bf[i].oe;
				ctl->dirty.bf.spoctl &= ~(0x1 << i);
			}
		}
		CTDPF("%s(%d) Writing spoctl = 0x%x\n", __func__, __LINE__, data);
		hw_write_20kx(hw, SPOCTL, data);
	}
	if (ctl->dirty.bf.spictl) {
		data = hw_read_20kx(hw, SPICTL);
		for (i = 0; i < 4; i++) {
			if ((ctl->dirty.bf.spictl & (0x1 << i))) {
				((spictl_t*)&data)->bf[i].en = ctl->spictl.bf[i].en;
				ctl->dirty.bf.spictl &= ~(0x1 << i);
			}
		}
		CTDPF("%s(%d) Writing spictl = 0x%x\n", __func__, __LINE__, data);
		hw_write_20kx(hw, SPICTL, data);
	}
	if (ctl->dirty.bf.daoimap) {
		CTDPF("%s(%d) Writing aim(%d) = 0x%x\n", __func__, __LINE__, ctl->daoimap.idx, ctl->daoimap.aim.data);
		hw_write_20kx(hw, DAOIMAP+ctl->daoimap.idx*4, ctl->daoimap.aim.data); 
		ctl->dirty.bf.daoimap = 0;
	}

	return 0;
}

static int daio_mgr_get_ctrl_blk(void **rblk)
{
	daio_mgr_ctrl_blk_t *blk;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != rblk);
	*rblk = NULL;
	blk = kzalloc(sizeof(*blk), GFP_KERNEL);
	if (NULL == blk) {
		CTDPF("%s(%d): Memory allocation failed!!!\n", __func__, __LINE__);
		return -ENOMEM;
	}

	*rblk = blk;

	return 0;
}

static int daio_mgr_put_ctrl_blk(void *blk)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != blk);

	kfree((daio_mgr_ctrl_blk_t*)blk);

	return 0;
}

/* Card hardware initialization block */
struct dac_conf {
	unsigned int msr; /* master sample rate in rsrs */
};

struct adc_conf {
	unsigned int msr; 	/* master sample rate in rsrs */
	unsigned char input; 	/* the input source of ADC */ 
	unsigned char mic20db; 	/* boost mic by 20db if input is microphone */ 
};

struct daio_conf {
	unsigned int msr; /* master sample rate in rsrs */
};

struct trn_conf {
	unsigned long vm_pgt_phys;
};

static int hw_daio_init(struct hw *hw, const struct daio_conf *info)
{
	u32 i2sorg = 0;
	u32 spdorg = 0;

	CTDPF("%s is called\n", __func__);
	CTASSERT(NULL != hw);
	CTASSERT(NULL != info);
	CTDPF("%s: msr = %d\n", __func__, info->msr);

	/* Read I2S CTL.  Keep original value. */
	/*i2sorg = hw_read_20kx(hw, I2SCTL);*/
	i2sorg = 0x94040404; /* enable all audio out and I2S-D input */ 
	/* Program I2S with proper master sample rate and enable 
	 * the correct I2S channel. */
	i2sorg &= 0xfffffffc;

	/* Enable S/PDIF-out-A in fixed 24-bit data format and default to 48kHz. */
	hw_write_20kx(hw, SPOCTL, 0x0); /* Disable all before doing any changes. */ 
	spdorg = 0x05;

	switch (info->msr) {
	case 1:
		i2sorg |= 1;
		spdorg |= (0x0 << 6); 
		break;
	case 2:
		i2sorg |= 2;
		spdorg |= (0x1 << 6); 
		break;
	case 4:
		i2sorg |= 3;
		spdorg |= (0x2 << 6); 
		break;
	default:
		i2sorg |= 1;
		break;
	}

	hw_write_20kx(hw, I2SCTL, i2sorg); 
	hw_write_20kx(hw, SPOCTL, spdorg); 

	/* Enable S/PDIF-in-A in fixed 24-bit data format. */
	hw_write_20kx(hw, SPICTL, 0x0); /* Disable all before doing any changes. */ 
	mdelay(1);
	spdorg = 0x0a0a0a0a;
	hw_write_20kx(hw, SPICTL, spdorg);
	mdelay(1);

	return 0;
}

/* TRANSPORT operations */
static int hw_trn_init(struct hw *hw, const struct trn_conf *info)
{
	u32 trnctl = 0;
	unsigned long ptp_phys_low = 0, ptp_phys_high = 0;

	CTDPF("%s is called\n", __func__);
	CTASSERT(NULL != hw);
	CTASSERT(NULL != info);

	/* Set up device page table */
	if ((~0UL) == info->vm_pgt_phys) {
		CTDPF("%s: Wrong device page table page addr!", __func__);
		return -1;
	}

	trnctl = 0x13;  /* 32-bit, 4k-size page */
#if BITS_PER_LONG == 64
	ptp_phys_low = info->vm_pgt_phys & ((1UL<<32)-1);
	ptp_phys_high = (info->vm_pgt_phys>>32) & ((1UL<<32)-1);
	trnctl |= (1<<2);
#elif BITS_PER_LONG == 32
	ptp_phys_low = info->vm_pgt_phys & (~0UL);
	ptp_phys_high = 0;
#else
#	error "Unknown BITS_PER_LONG!"
#endif
#if PAGE_SIZE == 8192
	trnctl |= (1<<5);
#endif
	CTDPF("%s: ptp_phys_low = 0x%p, ptp_phys_high = 0x%p, trnctl = 0x%x\n", __func__, ptp_phys_low, ptp_phys_high, trnctl);
	hw_write_20kx(hw, PTPALX, ptp_phys_low);
	hw_write_20kx(hw, PTPAHX, ptp_phys_high);
	hw_write_20kx(hw, TRNCTL, trnctl); 
	hw_write_20kx(hw, TRNIS, 0x200c01); /* realy needed? */

	return 0;
}

/* Card initialization */
typedef union {
	struct {
		u32 eac		: 1;  /* (0:0) */
		u32 eai		: 1;  /* (1:1) */
		u32 bep		: 1;  /* (2:2) */
		u32 bes		: 1;  /* (3:3) */
		u32 dsp		: 1;  /* (4:4) */
		u32 dbp		: 1;  /* (5:5) */
		u32 abp		: 1;  /* (6:6) */
		u32 tbp		: 1;  /* (7:7) */
		u32 sbp		: 1;  /* (8:8) */
		u32 fbp		: 1;  /* (9:9) */
		u32 xa		: 1;  /* (10:10) */
		u32 et		: 1;  /* (11:11) */
		u32 pr		: 1;  /* (12:12) */
		u32 mrl		: 1;  /* (13:13) */
		u32 sde		: 1;  /* (14:14) */
		u32 sdi		: 1;  /* (15:15) */
		u32 sm		: 1;  /* (16:16) */
		u32 sr		: 1;  /* (17:17) */
		u32 sd		: 1;  /* (18:18) */
		u32 se		: 1;  /* (19:19) */
		u32 aid		: 1;  /* (20:20) */
		u32 rsv 	: 11; /* (21:31) */
	} bf;
	u32 data;
} gctl_t;

typedef union {
	struct {
		u32 src		: 4;  /* (0:3) */
		u32 rd		: 4;  /* (4:7) */
		u32 fd		: 4;  /* (8:11) */
		u32 clk0	: 2;  /* (12:13) */
		u32 clk1	: 2;  /* (14:15) */
		u32 dcs		: 3;  /* (16:18) */
		u32 b		: 1;  /* (19:19) */
		u32 as		: 1;  /* (20:20) */
		u32 s30		: 1;  /* (21:21) */
		u32 sl		: 1;  /* (22:22) */
		u32 fas		: 1;  /* (23:23) */
		u32 frm		: 1;  /* (24:24) */
		u32 fr		: 1;  /* (25:25) */
		u32 l		: 1;  /* (26:26) */
		u32 t		: 2;  /* (27:28) */
		u32 oca		: 1;  /* (29:29) */
		u32 nca		: 1;  /* (30:30) */
		u32 rsv		: 1;  /* (31:31) */
	} bf;
	u32 data;
} pllctl_t;

typedef union {
	u32 data;
} gpioctl_t;

static int hw_pll_init(struct hw *hw, unsigned int rsr)
{
	pllctl_t pllctl;
	int i = 0;

	pllctl.data = (48000 == rsr) ? 0x1480a001 : 0x1480a731;
	for (i = 0; i < 3; i++) {
		CTDPF("%s: pllctl read = 0x%x\n", __func__, hw_read_20kx(hw, PLLCTL));
		if (hw_read_20kx(hw, PLLCTL) == pllctl.data) {
			break;
		}
		hw_write_20kx(hw, PLLCTL, pllctl.data);
		mdelay(40);
	}
	if (i >= 3) {
		printk(KERN_ALERT "PLL initialization failed!!!\n");
		return -EBUSY;
	}

	return 0;
}

static int hw_auto_init(struct hw *hw)
{
	gctl_t gctl; 
	int i;

	gctl.data = hw_read_20kx(hw, GCTL);
	CTDPF("%s: gctl = 0x%x\n", __func__, gctl.data);
	gctl.bf.eai = 0;
	hw_write_20kx(hw, GCTL, gctl.data);
	gctl.bf.eai = 1;
	hw_write_20kx(hw, GCTL, gctl.data);
	mdelay(10);
	for (i = 0; i < 400000; i++) {
		gctl.data = hw_read_20kx(hw, GCTL);
		if (gctl.bf.aid) {
			break;
		}
	}
	CTDPF("%s: gctl = 0x%x\n", __func__, gctl.data);
	if (!gctl.bf.aid) {
		printk(KERN_ALERT "Card Auto-init failed!!!\n");
		return -EBUSY;
	}

	return 0;
}

static int i2c_unlock(struct hw *hw)
{
	if ((hw_read_pci(hw, 0xcc) & 0xff) == 0xaa) {
		return 0;
	}

	hw_write_pci(hw, 0xcc, 0x8c);   
	hw_write_pci(hw, 0xcc, 0x0e);
	if ((hw_read_pci(hw, 0xcc) & 0xff) == 0xaa) {
		return 0;
	}

	hw_write_pci(hw, 0xcc, 0xee);
	hw_write_pci(hw, 0xcc, 0xaa);
	if ((hw_read_pci(hw, 0xcc) & 0xff) == 0xaa) {
		return 0;
	}

	return -1;
}

static void i2c_lock(struct hw *hw)
{
	if ((hw_read_pci(hw, 0xcc) & 0xff) == 0xaa) {
		hw_write_pci(hw, 0xcc, 0x00);   
	}
}

static void i2c_write(struct hw *hw, u32 device, u32 addr, u32 data)
{
	while(!(hw_read_pci(hw, 0xEC) & 0x800000));
	hw_write_pci(hw, 0xE0, device);
	hw_write_pci(hw, 0xE4, (data << 8) | (addr & 0xff));	 
}

/* DAC operations */

static int hw_reset_dac(struct hw *hw)
{
	u32 i = 0;
	u16 gpioorg = 0;

	CTDPF("%s is called\n", __func__);

	if (i2c_unlock(hw)) {
		return -1;
	}

	while(!(hw_read_pci(hw, 0xEC) & 0x800000));
	hw_write_pci(hw, 0xEC, 0x05);  /* write to i2c status control */

	/* To be effective, need to reset the DAC twice. */
	for (i = 0; i < 2;  i++) {
		/* set gpio */
		mdelay(100);
		gpioorg = (u16)hw_read_20kx(hw, GPIO);
		gpioorg &= 0xfffd;
		hw_write_20kx(hw, GPIO, gpioorg);
		mdelay(1);
		hw_write_20kx(hw, GPIO, gpioorg | 0x2);
	}	

	i2c_write(hw, 0x00180080, 0x01, 0x80);
	i2c_write(hw, 0x00180080, 0x02, 0x10);

	i2c_lock(hw);

	return 0;
}

static int hw_dac_init(struct hw *hw, const struct dac_conf *info)
{
	u32 data = 0;
	u16 gpioorg = 0;
	u16 subsys_id = 0;

	CTDPF("%s is called\n", __func__);
	CTASSERT(NULL != hw);
	CTASSERT(NULL != info);

	pci_read_config_word(hw->pci, PCI_SUBSYSTEM_ID, &subsys_id);
	CTDPF("%s: msr = %d, subsysid = 0x%x\n", __func__, info->msr, subsys_id);
	if ((subsys_id == 0x0022) || (subsys_id == 0x002F)) {
		/* SB055x, unmute outputs */
		gpioorg=(u16)hw_read_20kx(hw, GPIO);
		CTDPF("%s: SB055x default gpioorg = 0x%x\n", __func__, gpioorg);
		gpioorg &= 0xffbf;	/* set GPIO6 to low */
		gpioorg |= 2;		/* set GPIO1 to high */
		hw_write_20kx(hw, GPIO, gpioorg);
		return 0;
	}

	/* mute outputs */
	gpioorg = (u16)hw_read_20kx(hw, GPIO);
	gpioorg &= 0xffbf;
	hw_write_20kx(hw, GPIO, gpioorg);
    
	hw_reset_dac(hw);

	if (i2c_unlock(hw)) {
		return -1;
	}
	
	hw_write_pci(hw, 0xEC, 0x05);  /* write to i2c status control */
	while(!(hw_read_pci(hw, 0xEC) & 0x800000));

	switch (info->msr) {
	case 1:
		data = 0x24;
		break;
	case 2:
		data = 0x25;
		break;
	case 4:
		data = 0x26;
		break;
	default:
		data = 0x24;
		break;
	}

	i2c_write(hw, 0x00180080, 0x06, data);
	i2c_write(hw, 0x00180080, 0x09, data);
	i2c_write(hw, 0x00180080, 0x0c, data);
	i2c_write(hw, 0x00180080, 0x0f, data);

	i2c_lock(hw);

	/* unmute outputs */
	gpioorg = (u16)hw_read_20kx(hw, GPIO);
	gpioorg = gpioorg | 0x40;
	hw_write_20kx(hw, GPIO, gpioorg);

	return 0;
}

/* ADC operations */

static int is_adc_input_selected_SB055x(struct hw *hw, enum ADCSRC type)
{
	u32 data = 0;
#if 0
	data = hw_read_20kx(hw, GPIO);
	switch (type) {
	case ADC_MICIN: 
		data = ((data & (0x1<<7)) && !(data & (0x1<<8)) && 
			(data & (0x1<<9)) && !(data & (0x1<<12)));
		break;
	case ADC_LINEIN:
		data = (!(data & (0x1<<2)) && !(data & (0x1<<7)) && 
			!(data & (0x1<<8)) && !(data & (0x1<<9)) && 
			!(data & (0x1<<12)));
		break;
	case ADC_AUX:
		data = (!(data & (0x1<<2)) && !(data & (0x1<<7)) && 
			!(data & (0x1<<8)) && !(data & (0x1<<9)) && 
			(data & (0x1<<12)));
		break;
	case ADC_NONE: /* Digital I/O */
		data = (!(data & (0x1<<8)) && (data & (0x1<<12)));
		break;
	default:
		data = 0;
	}
	CTDPF("%s: type = %d, return %d\n", __func__, type, data);
#endif
	return data;
}

static int is_adc_input_selected_SBx(struct hw *hw, enum ADCSRC type)
{
	u32 data = 0;

	data = hw_read_20kx(hw, GPIO);
	switch (type) {
	case ADC_MICIN: 
		data = ((data & (0x1<<7)) && (data & (0x1<<8)));
		break;
	case ADC_LINEIN:
		data = (!(data & (0x1<<7)) && (data & (0x1<<8)));
		break;
	case ADC_NONE: /* Digital I/O */
		data = (!(data & (0x1<<8)));
		break;
	default:
		data = 0;
	}
	return data;
}

static int is_adc_input_selected_hendrix(struct hw *hw, enum ADCSRC type)
{
	u32 data = 0;

	data = hw_read_20kx(hw, GPIO);
	switch (type) {
	case ADC_MICIN: 
		data = (data & (0x1 << 7)) ? 1 : 0;
		break;
	case ADC_LINEIN:
		data = (data & (0x1 << 7)) ? 0 : 1;
		break;
	default:
		data = 0;
	}
	return data;
}

static int hw_is_adc_input_selected(struct hw *hw, enum ADCSRC type)
{
	u16 subsys_id = 0;

	pci_read_config_word(hw->pci, PCI_SUBSYSTEM_ID, &subsys_id);
    	if ((subsys_id == 0x0022) || (subsys_id == 0x002F)) {
		/* SB055x cards */
		return is_adc_input_selected_SB055x(hw, type);
	} else if ((subsys_id == 0x0029) || (subsys_id == 0x0031)) {
		/* SB073x cards */ 
		return is_adc_input_selected_hendrix(hw, type);
	} else if ((subsys_id & 0xf000) == 0x6000) {
		/* Vista compatible cards */
		return is_adc_input_selected_hendrix(hw, type);
	} else {
		return is_adc_input_selected_SBx(hw, type);
	}
}

static int 
adc_input_select_SB055x(struct hw *hw, enum ADCSRC type, unsigned char boost)
{
	u32 data = 0;

	/* 
	 * check and set the following GPIO bits accordingly
	 * ADC_Gain		= GPIO2
	 * DRM_off		= GPIO3
	 * Mic_Pwr_on		= GPIO7 
	 * Digital_IO_Sel	= GPIO8	  
	 * Mic_Sw		= GPIO9
	 * Aux/MicLine_Sw	= GPIO12
	 */
	data = hw_read_20kx(hw, GPIO);
	data &= 0xec73;
	switch (type) {
	case ADC_MICIN:
		data |= (0x1<<7) | (0x1<<8) | (0x1<<9) ;
		data |= boost ? (0x1<<2) : 0;
		break;
	case ADC_LINEIN:
		data |= (0x1<<8);
		break;
	case ADC_AUX:
		data |= (0x1<<8) | (0x1<<12);
		break;
	case ADC_NONE:
		data |= (0x1<<12);  /* set to digital */
		break;
	default:
		return -1;
	}

	CTDPF("%s: type = %d, Writing GPIO 0x%x\n", __func__, type, data);
	hw_write_20kx(hw, GPIO, data);  
  
	return 0;
}


static int 
adc_input_select_SBx(struct hw *hw, enum ADCSRC type, unsigned char boost)
{
	u32 data = 0;
	u32 i2c_data = 0;

	if (i2c_unlock(hw)) {
		return -1;
	}
		
	while(!(hw_read_pci(hw, 0xEC) & 0x800000)); /* i2c ready poll */
	/* set i2c access mode as Direct Control */
	hw_write_pci(hw, 0xEC, 0x05);

	data = hw_read_20kx(hw, GPIO);
	switch (type) {
	case ADC_MICIN: 
		data |= ((0x1 << 7) | (0x1 << 8));
		i2c_data = 0x1;  /* Mic-in */
		break;
	case ADC_LINEIN:
		data &= ~(0x1 << 7);
		data |= (0x1 << 8);
		i2c_data = 0x2; /* Line-in */
		break;
	case ADC_NONE:
		data &= ~(0x1 << 8);
		i2c_data = 0x0; /* set to Digital */
		break;
	default:
		i2c_lock(hw);
		return -1;
	}
	hw_write_20kx(hw, GPIO, data);
	i2c_write(hw, 0x001a0080, 0x2a, i2c_data);
	if (boost) {
		i2c_write(hw, 0x001a0080, 0x1c, 0xe7); /* +12dB boost */
		i2c_write(hw, 0x001a0080, 0x1e, 0xe7); /* +12dB boost */
	} else {
		i2c_write(hw, 0x001a0080, 0x1c, 0xcf); /* No boost */
		i2c_write(hw, 0x001a0080, 0x1e, 0xcf); /* No boost */
	}

	i2c_lock(hw);

	return 0;
}

static int 
adc_input_select_hendrix(struct hw *hw, enum ADCSRC type, unsigned char boost)
{
	u32 data = 0;
	u32 i2c_data = 0;

	if (i2c_unlock(hw)) {
		return -1;
	}
		
	while(!(hw_read_pci(hw, 0xEC) & 0x800000)); /* i2c ready poll */
	/* set i2c access mode as Direct Control */
	hw_write_pci(hw, 0xEC, 0x05); 

	data = hw_read_20kx(hw, GPIO);
	switch (type) {
	case ADC_MICIN: 
		data |= (0x1 << 7);
		i2c_data = 0x1;  /* Mic-in */
		break;
	case ADC_LINEIN:
		data &= ~(0x1 << 7);
		i2c_data = 0x2; /* Line-in */
		break;
	default:
		i2c_lock(hw);
		return -1;
	}
	CTDPF("%s: type = %d, Writing GPIO 0x%x\n", __func__, type, data);
	hw_write_20kx(hw, GPIO, data);
	i2c_write(hw, 0x001a0080, 0x2a, i2c_data);
	if (boost) {
		i2c_write(hw, 0x001a0080, 0x1c, 0xe7); /* +12dB boost */
		i2c_write(hw, 0x001a0080, 0x1e, 0xe7); /* +12dB boost */
	} else {
		i2c_write(hw, 0x001a0080, 0x1c, 0xcf); /* No boost */
		i2c_write(hw, 0x001a0080, 0x1e, 0xcf); /* No boost */
	}

	i2c_lock(hw);

	return 0;
}

static int hw_adc_input_select(struct hw *hw, enum ADCSRC type)
{
	u16 subsys_id = 0;

	pci_read_config_word(hw->pci, PCI_SUBSYSTEM_ID, &subsys_id);
    	if ((subsys_id == 0x0022) || (subsys_id == 0x002F)) {
		/* SB055x cards */
		return adc_input_select_SB055x(hw, type, (ADC_MICIN == type));
	} else if ((subsys_id == 0x0029) || (subsys_id == 0x0031)) {
		/* SB073x cards */ 
		return adc_input_select_hendrix(hw, type, (ADC_MICIN == type));
	} else if ((subsys_id & 0xf000) == 0x6000) {
		/* Vista compatible cards */
		return adc_input_select_hendrix(hw, type, (ADC_MICIN == type));
	} else {
		return adc_input_select_SBx(hw, type, (ADC_MICIN == type));
	}
}

static int adc_init_SB055x(struct hw *hw, int input, int mic20db)
{
	return adc_input_select_SB055x(hw, input, mic20db);
}

static int adc_init_SBx(struct hw *hw, int input, int mic20db)
{
	u16 gpioorg;
	u16 input_source;
	u32 adcdata = 0;

	input_source = 0x100;  /* default to analog */
	switch(input) {
	case ADC_MICIN:
		adcdata = 0x1;
		input_source = 0x180;  /* set GPIO7 to select Mic */
		break;
	case ADC_LINEIN:
		adcdata = 0x2;
		break;
	case ADC_VIDEO:
		adcdata = 0x4;
		break;
	case ADC_AUX:
		adcdata = 0x8;
		break;
	case ADC_NONE:
		adcdata = 0x0;
		input_source = 0x0;  /* set to Digital */
		break;
	default:
		break;
	}

	if (i2c_unlock(hw)) {
		return -1;
	}
		
	while(!(hw_read_pci(hw, 0xEC) & 0x800000)); /* i2c ready poll */
	hw_write_pci(hw, 0xEC, 0x05);  /* write to i2c status control */

	i2c_write(hw, 0x001a0080, 0x0e, 0x08);
	i2c_write(hw, 0x001a0080, 0x18, 0x0a);
	i2c_write(hw, 0x001a0080, 0x28, 0x86);
	i2c_write(hw, 0x001a0080, 0x2a, adcdata);

	if (mic20db) {
		i2c_write(hw, 0x001a0080, 0x1c, 0xf7);
		i2c_write(hw, 0x001a0080, 0x1e, 0xf7);
	} else {
		i2c_write(hw, 0x001a0080, 0x1c, 0xcf);
		i2c_write(hw, 0x001a0080, 0x1e, 0xcf);
	}

	if (!(hw_read_20kx(hw, ID0) & 0x100)) {
		i2c_write(hw, 0x001a0080, 0x16, 0x26);
	}

	i2c_lock(hw);

	gpioorg = (u16)hw_read_20kx(hw,  GPIO);
	gpioorg &= 0xfe7f;
	gpioorg |= input_source;
	hw_write_20kx(hw, GPIO, gpioorg);  
	
	return 0;
}

static int hw_adc_init(struct hw *hw, const struct adc_conf *info)
{
	int err = 0;
	u16 subsys_id = 0;

	CTDPF("%s is called\n", __func__);
	CTASSERT(NULL != hw);
	CTASSERT(NULL != info);

	pci_read_config_word(hw->pci, PCI_SUBSYSTEM_ID, &subsys_id);
	if ((subsys_id == 0x0022) || (subsys_id == 0x002F)) {
		/* Sb055x card */
		err = adc_init_SB055x(hw, info->input, info->mic20db);
	} else {
		err = adc_init_SBx(hw, info->input, info->mic20db);
	}

	return err;
}

static int hw_have_digit_io_switch(struct hw *hw)
{
	u16 subsys_id = 0;

	pci_read_config_word(hw->pci, PCI_SUBSYSTEM_ID, &subsys_id);
	/* SB073x and Vista compatible cards have no digit IO switch */ 
	return !((subsys_id == 0x0029) || (subsys_id == 0x0031) 
				|| ((subsys_id & 0xf000) == 0x6000));
}

#define UAA_CFG_PWRSTATUS	0x44
#define UAA_CFG_SPACE_FLAG	0xA0
#define UAA_CORE_CHANGE		0x3FFC
static int uaa_to_xfi(struct pci_dev *pci)
{
	unsigned int bar0, bar1, bar2, bar3, bar4, bar5;
	unsigned int cmd, irq, cl_size, l_timer, pwr;
	unsigned int CTLA, CTLZ, CTLL, CTLX, CTL_, CTLF, CTLi;
	unsigned int is_uaa = 0;
	unsigned int data[4] = {0};
	unsigned int io_base;
	void *mem_base;
	int i = 0;

	CTDPF("%s is called\n", __func__);

	/* By default, Hendrix card UAA Bar0 should be using memory... */
	io_base = pci_resource_start(pci, 0);
	mem_base = ioremap(io_base, pci_resource_len(pci, 0));
	if (NULL == mem_base) {
		return -ENOENT;
	}
	   
	CTDPF("%s(%d) here!\n", __func__, __LINE__);
	CTLX = ___constant_swab32(*((unsigned int *)"CTLX"));
	CTL_ = ___constant_swab32(*((unsigned int *)"CTL-"));
	CTLF = ___constant_swab32(*((unsigned int *)"CTLF"));
	CTLi = ___constant_swab32(*((unsigned int *)"CTLi"));
	CTLA = ___constant_swab32(*((unsigned int *)"CTLA"));
	CTLZ = ___constant_swab32(*((unsigned int *)"CTLZ"));
	CTLL = ___constant_swab32(*((unsigned int *)"CTLL"));
	CTDPF("%s: CTLA = 0x%x, CTLZ = 0x%x, CTLL = 0x%x, CTLX = 0x%x, CTL_ = 0x%x, CTLF = 0x%x, CTLi = 0x%x\n", __func__, CTLA, CTLZ, CTLL, CTLX, CTL_, CTLF, CTLi);

	/* Read current mode from Mode Change Register */
	for (i = 0; i < 4; i++) {
		data[i] = readl(mem_base + UAA_CORE_CHANGE);
		CTDPF("%s(%d) here! data[%d] = 0x%x\n", __func__, __LINE__, i, data[i]);
	}

	/* Determine current mode... */
	if (data[0] == CTLA) {
		is_uaa = ((data[1] == CTLZ && data[2] == CTLL && data[3] == CTLA) ||
			 (data[1] == CTLA && data[2] == CTLZ && data[3] == CTLL));
	} else if (data[0] == CTLZ) {
		is_uaa = (data[1] == CTLL && data[2] == CTLA && data[3] == CTLA);
	} else if (data[0] == CTLL) { 
		is_uaa = (data[1] == CTLA && data[2] == CTLA && data[3] == CTLZ);
	} else {
		is_uaa = 0;
	}

	CTDPF("%s(%d) here! is_uaa = %u\n", __func__, __LINE__, is_uaa);
	if (!is_uaa) {
		/* Not in UAA mode currently. Return directly. */
		iounmap(mem_base);
		return 0;
	}

	pci_read_config_dword(pci, PCI_BASE_ADDRESS_0, &bar0);
	pci_read_config_dword(pci, PCI_BASE_ADDRESS_1, &bar1);
	pci_read_config_dword(pci, PCI_BASE_ADDRESS_2, &bar2);
	pci_read_config_dword(pci, PCI_BASE_ADDRESS_3, &bar3);
	pci_read_config_dword(pci, PCI_BASE_ADDRESS_4, &bar4);
	pci_read_config_dword(pci, PCI_BASE_ADDRESS_5, &bar5);
	pci_read_config_dword(pci, PCI_INTERRUPT_LINE, &irq);
	pci_read_config_dword(pci, PCI_CACHE_LINE_SIZE, &cl_size);
	pci_read_config_dword(pci, PCI_LATENCY_TIMER, &l_timer);
	pci_read_config_dword(pci, UAA_CFG_PWRSTATUS, &pwr);
	pci_read_config_dword(pci, PCI_COMMAND, &cmd);

	CTDPF("%s: UAA mode bar0 = 0x%x, bar1 = 0x%x, bar2 = 0x%x, bar3 = 0x%x, bar4 = 0x%x, bar5 = 0x%x\n", __func__, bar0, bar1, bar2, bar3, bar4, bar5);

	/* Set up X-Fi core PCI configuration space. */
	/* Switch to X-Fi config space with BAR0 exposed. */
	pci_write_config_dword(pci, UAA_CFG_SPACE_FLAG, 0x87654321); 
	/* Copy UAA's BAR5 into X-Fi BAR0 */
	pci_write_config_dword(pci, PCI_BASE_ADDRESS_0, bar5);       
	/* Switch to X-Fi config space without BAR0 exposed. */
	pci_write_config_dword(pci, UAA_CFG_SPACE_FLAG, 0x12345678);
	pci_write_config_dword(pci, PCI_BASE_ADDRESS_1, bar1);
	pci_write_config_dword(pci, PCI_BASE_ADDRESS_2, bar2);
	pci_write_config_dword(pci, PCI_BASE_ADDRESS_3, bar3);
	pci_write_config_dword(pci, PCI_BASE_ADDRESS_4, bar4);
	pci_write_config_dword(pci, PCI_INTERRUPT_LINE, irq);
	pci_write_config_dword(pci, PCI_CACHE_LINE_SIZE, cl_size);
	pci_write_config_dword(pci, PCI_LATENCY_TIMER, l_timer);
	pci_write_config_dword(pci, UAA_CFG_PWRSTATUS, pwr);
	pci_write_config_dword(pci, PCI_COMMAND, cmd);

	/* Switch to X-Fi mode */
	writel(CTLX, (mem_base + UAA_CORE_CHANGE));
	writel(CTL_, (mem_base + UAA_CORE_CHANGE));
	writel(CTLF, (mem_base + UAA_CORE_CHANGE));
	writel(CTLi, (mem_base + UAA_CORE_CHANGE));

#ifdef DEBUG
	pci_read_config_dword(pci, PCI_BASE_ADDRESS_0, &bar0);
	pci_read_config_dword(pci, PCI_BASE_ADDRESS_1, &bar1);
	pci_read_config_dword(pci, PCI_BASE_ADDRESS_2, &bar2);
	pci_read_config_dword(pci, PCI_BASE_ADDRESS_3, &bar3);
	pci_read_config_dword(pci, PCI_BASE_ADDRESS_4, &bar4);
	pci_read_config_dword(pci, PCI_BASE_ADDRESS_5, &bar5);

	CTDPF("%s: UAA mode bar0 = 0x%x, bar1 = 0x%x, bar2 = 0x%x, bar3 = 0x%x, bar4 = 0x%x, bar5 = 0x%x\n", __func__, bar0, bar1, bar2, bar3, bar4, bar5);
#endif
	iounmap(mem_base);

	return 0;
}

static int hw_card_start(struct hw *hw)
{
	int err = 0;
	struct pci_dev *pci = hw->pci;
	u16 subsys_id = 0;
	unsigned int dma_mask = 0;

	CTDPF("%s is called\n", __func__);
	if ((err = pci_enable_device(pci)) < 0) {
		return err;
	}

	/* Set DMA transfer mask */
	dma_mask = 0xffffffffUL;
	if (pci_set_dma_mask(pci, dma_mask) < 0 ||
	    pci_set_consistent_dma_mask(pci, dma_mask) < 0) {
		printk(KERN_ERR "architecture does not support PCI \
		busmaster DMA with mask 0x%x\n", dma_mask);
		err = -ENXIO;
		goto error1;
	}

	if ((err = pci_request_regions(pci, "XFi")) < 0) {
		goto error1;
	}
	
	/* Switch to X-Fi mode from UAA mode if neeeded */
	pci_read_config_word(pci, PCI_SUBSYSTEM_ID, &subsys_id);
	if ((0x5 == pci->device) && (0x6000 == (subsys_id & 0x6000))) {
		if ((err = uaa_to_xfi(pci))) {
			goto error2;
		}
		hw->io_base = pci_resource_start(pci, 5);
	} else {
		hw->io_base = pci_resource_start(pci, 0);
	}

	/*if ((err = request_irq(pci->irq, ct_atc_interrupt, IRQF_SHARED, 
				atc->chip_details->nm_card, hw))) {
		goto error2;
	}
	hw->irq = pci->irq;
	*/

	pci_set_master(pci);

	return 0;

error2:
	pci_release_regions(pci);
	hw->io_base = 0;
error1:
	pci_disable_device(pci);
	return err;
}

static int hw_card_stop(struct hw *hw)
{
	/* TODO: Disable interrupt and so on... */
	return 0;
}

static int hw_card_shutdown(struct hw *hw)
{
	if(hw->irq >= 0) {
		free_irq(hw->irq, hw);
	}
	hw->irq	= -1;

	if (NULL != ((void*)hw->mem_base)) {
		iounmap((void*)hw->mem_base);
	}
	hw->mem_base = (unsigned long)NULL;

	if (hw->io_base) {
		pci_release_regions(hw->pci);
	}
	hw->io_base = 0;

	pci_disable_device(hw->pci);

	return 0;
}

static int hw_card_init(struct hw *hw, struct card_conf *info)
{
	int err;
	gctl_t gctl; 
	u16 subsys_id = 0;
	u32 data = 0;
	struct dac_conf dac_info = {0};
	struct adc_conf adc_info = {0};
	struct daio_conf daio_info = {0};
	struct trn_conf trn_info = {0};
#ifdef DEBUG
	u32 iter_count = 0;
#endif

	CTDPF("%s is called\n", __func__);
	CTASSERT(NULL != hw);
	CTASSERT(NULL != info);

	/* Get PCI io port base address and do Hendrix switch if needed. */
	if (!hw->io_base) {
		if ((err = hw_card_start(hw))) {
			return err;
		}
	}
#ifdef DEBUG
	for (iter_count = 0; iter_count < 6; iter_count++) {
		CTDPF("%s: BAR %d start = 0x%x size = 0x%x flags = 0x%x\n", __func__, iter_count, pci_resource_start(hw->pci, iter_count), pci_resource_len(hw->pci, iter_count), pci_resource_flags(hw->pci, iter_count));
	}
	CTDPF("%s: io_base = 0x%x mem_base = 0x%lx\n", __func__, hw->io_base, hw->mem_base);
#endif

	/* PLL init */
	if ((err = hw_pll_init(hw, info->rsr)) < 0) {
		return err;
	}

	/* kick off auto-init */
	if ((err = hw_auto_init(hw)) < 0) {
		return err;
	}

	/* Enable audio ring */
	gctl.data = hw_read_20kx(hw, GCTL);
	gctl.bf.eac = 1;
	gctl.bf.dbp = 1;
	gctl.bf.tbp = 1;
	gctl.bf.fbp = 1;
	gctl.bf.et = 1;
	hw_write_20kx(hw, GCTL, gctl.data);
	mdelay(10);

	/* Reset all global pending interrupts */
	hw_write_20kx(hw, GIE, 0);
	/* Reset all SRC pending interrupts */
	hw_write_20kx(hw, SRCIP, 0);
	mdelay(30);

	pci_read_config_word(hw->pci, PCI_SUBSYSTEM_ID, &subsys_id);
	/* Detect the card ID and configure GPIO accordingly. */
    	if ((subsys_id == 0x0022) || (subsys_id == 0x002F)) {
		/* SB055x cards */
		hw_write_20kx(hw, GPIOCTL, 0x13fe);
	} else if ((subsys_id == 0x0029) || (subsys_id == 0x0031)) {
		/* SB073x cards */ 
		hw_write_20kx(hw, GPIOCTL, 0x00e6);
	} else if ((subsys_id & 0xf000) == 0x6000) {
		/* Vista compatible cards */
		hw_write_20kx(hw, GPIOCTL, 0x00c2);
	} else {
		hw_write_20kx(hw, GPIOCTL, 0x01e6);
	}

	trn_info.vm_pgt_phys = info->vm_pgt_phys;
	if ((err = hw_trn_init(hw, &trn_info)) < 0) {
		return err;
	}

	daio_info.msr = info->msr;
	if ((err = hw_daio_init(hw, &daio_info)) < 0) {
		return err;
	}

	dac_info.msr = info->msr;
	if ((err = hw_dac_init(hw, &dac_info)) < 0) {
		return err;
	}

	adc_info.msr = info->msr;
	adc_info.input = ADC_LINEIN;
	adc_info.mic20db = 0;
	if ((err = hw_adc_init(hw, &adc_info)) < 0) {
		return err;
	}
	
	data = hw_read_20kx(hw, SRCMCTL);
	data |= 0x1; /* Enables input from the audio ring */
	hw_write_20kx(hw, SRCMCTL, data);

	return 0;
}

static u32 hw_read_20kx(struct hw *hw, u32 reg)
{
	u32 value;
	unsigned long flags;

	spin_lock_irqsave(&container_of(hw, struct hw20k1, hw)->reg_20k1_lock, flags);
	outl(reg, hw->io_base + 0x0);
	value = inl(hw->io_base + 0x4);
	spin_unlock_irqrestore(&container_of(hw, struct hw20k1, hw)->reg_20k1_lock, flags);

	return value;
}	

static void hw_write_20kx(struct hw *hw, u32 reg, u32 data)
{
	unsigned long flags;

	spin_lock_irqsave(&container_of(hw, struct hw20k1, hw)->reg_20k1_lock, flags);
	outl(reg, hw->io_base + 0x0);
	outl(data, hw->io_base + 0x4);
	spin_unlock_irqrestore(&container_of(hw, struct hw20k1, hw)->reg_20k1_lock, flags);

}

static u32 hw_read_pci(struct hw *hw, u32 reg)
{
	u32 value;
	unsigned long flags;

	spin_lock_irqsave(&container_of(hw, struct hw20k1, hw)->reg_pci_lock, flags);
	outl(reg, hw->io_base + 0x10);
	value = inl(hw->io_base + 0x14);
	spin_unlock_irqrestore(&container_of(hw, struct hw20k1, hw)->reg_pci_lock, flags);

	return value;
}

static void hw_write_pci(struct hw *hw, u32 reg, u32 data)
{
	unsigned long flags;

	spin_lock_irqsave(&container_of(hw, struct hw20k1, hw)->reg_pci_lock, flags);
	outl(reg, hw->io_base + 0x10);
	outl(data, hw->io_base + 0x14);
	spin_unlock_irqrestore(&container_of(hw, struct hw20k1, hw)->reg_pci_lock, flags);
}

int create_20k1_hw_obj(hw_t **rhw)
{
	hw_t *hw;
	struct hw20k1 *hw20k1;

	CTASSERT(NULL != rhw);
	*rhw = NULL;
	hw20k1 = kzalloc(sizeof(*hw20k1), GFP_KERNEL);
	if (NULL == hw20k1) {
		CTDPF("%s(%d): Memory allocation failed!!!", __func__, __LINE__);
		return -ENOMEM;
	}
	
	spin_lock_init(&hw20k1->reg_20k1_lock);
	spin_lock_init(&hw20k1->reg_pci_lock);

	hw = &hw20k1->hw;

	hw->io_base = 0;
	hw->mem_base = (unsigned long)NULL;
	hw->irq = -1;

	hw->card_init = hw_card_init;
	hw->card_stop = hw_card_stop;
	hw->is_adc_source_selected = hw_is_adc_input_selected;
	hw->select_adc_source = hw_adc_input_select;
	/*hw->line_out_unmute = hw_line_out_unmute;*/
	hw->have_digit_io_switch = hw_have_digit_io_switch;

	hw->src_rsc_get_ctrl_blk = src_get_rsc_ctrl_blk;
	hw->src_rsc_put_ctrl_blk = src_put_rsc_ctrl_blk;
	hw->src_mgr_get_ctrl_blk = src_mgr_get_ctrl_blk;
	hw->src_mgr_put_ctrl_blk = src_mgr_put_ctrl_blk;
	hw->src_set_state = src_set_state;
	hw->src_set_bm = src_set_bm;
	hw->src_set_rsr = src_set_rsr;
	hw->src_set_sf = src_set_sf;
	hw->src_set_wr = src_set_wr;
	hw->src_set_pm = src_set_pm;
	hw->src_set_rom = src_set_rom;
	hw->src_set_vo = src_set_vo;
	hw->src_set_st = src_set_st;
	hw->src_set_ie = src_set_ie;
	hw->src_set_ilsz = src_set_ilsz;
	hw->src_set_bp = src_set_bp;
	hw->src_set_cisz = src_set_cisz;
	hw->src_set_ca = src_set_ca;
	hw->src_set_sa = src_set_sa;
	hw->src_set_la = src_set_la;
	hw->src_set_pitch = src_set_pitch;
	hw->src_set_dirty = src_set_dirty;
	hw->src_set_clear_zbufs = src_set_clear_zbufs;
	hw->src_set_dirty_all = src_set_dirty_all;
	hw->src_commit_write = src_commit_write;
	hw->src_get_ca = src_get_ca;
	hw->src_get_dirty = src_get_dirty;
	hw->src_dirty_conj_mask = src_dirty_conj_mask;
	hw->src_mgr_enbs_src = src_mgr_enbs_src;
	hw->src_mgr_enb_src = src_mgr_enb_src;
	hw->src_mgr_dsb_src = src_mgr_dsb_src;
	hw->src_mgr_commit_write = src_mgr_commit_write;

	hw->srcimp_mgr_get_ctrl_blk = srcimp_mgr_get_ctrl_blk;
	hw->srcimp_mgr_put_ctrl_blk = srcimp_mgr_put_ctrl_blk;
	hw->srcimp_mgr_set_imaparc = srcimp_mgr_set_imaparc;
	hw->srcimp_mgr_set_imapuser = srcimp_mgr_set_imapuser;
	hw->srcimp_mgr_set_imapnxt = srcimp_mgr_set_imapnxt;
	hw->srcimp_mgr_set_imapaddr = srcimp_mgr_set_imapaddr;
	hw->srcimp_mgr_commit_write = srcimp_mgr_commit_write;

	hw->amixer_rsc_get_ctrl_blk = amixer_rsc_get_ctrl_blk;
	hw->amixer_rsc_put_ctrl_blk = amixer_rsc_put_ctrl_blk;
	hw->amixer_mgr_get_ctrl_blk = amixer_mgr_get_ctrl_blk;
	hw->amixer_mgr_put_ctrl_blk = amixer_mgr_put_ctrl_blk;
	hw->amixer_set_mode = amixer_set_mode;
	hw->amixer_set_iv = amixer_set_iv;
	hw->amixer_set_x = amixer_set_x;
	hw->amixer_set_y = amixer_set_y;
	hw->amixer_set_sadr = amixer_set_sadr;
	hw->amixer_set_se = amixer_set_se;
	hw->amixer_set_dirty = amixer_set_dirty;
	hw->amixer_set_dirty_all = amixer_set_dirty_all;
	hw->amixer_commit_write = amixer_commit_write;
	hw->amixer_get_y = amixer_get_y;
	hw->amixer_get_dirty = amixer_get_dirty;

	hw->dai_get_ctrl_blk = dai_get_ctrl_blk;
	hw->dai_put_ctrl_blk = dai_put_ctrl_blk;
	hw->dai_srt_set_srco = dai_srt_set_srcr;
	hw->dai_srt_set_srcm = dai_srt_set_srcl;
	hw->dai_srt_set_rsr = dai_srt_set_rsr;
	hw->dai_srt_set_drat = dai_srt_set_drat;
	hw->dai_srt_set_ec = dai_srt_set_ec;
	hw->dai_srt_set_et = dai_srt_set_et;
	hw->dai_commit_write = dai_commit_write;

	hw->daio_mgr_get_ctrl_blk = daio_mgr_get_ctrl_blk;
	hw->daio_mgr_put_ctrl_blk = daio_mgr_put_ctrl_blk;
	hw->daio_mgr_enb_dai = daio_mgr_enb_dai;
	hw->daio_mgr_dsb_dai = daio_mgr_dsb_dai;
	hw->daio_mgr_enb_dao = daio_mgr_enb_dao;
	hw->daio_mgr_dsb_dao = daio_mgr_dsb_dao;
	hw->daio_mgr_set_imaparc = daio_mgr_set_imaparc;
	hw->daio_mgr_set_imapnxt = daio_mgr_set_imapnxt;
	hw->daio_mgr_set_imapaddr = daio_mgr_set_imapaddr;
	hw->daio_mgr_commit_write = daio_mgr_commit_write;

	*rhw = hw;

	return 0;
}

int destroy_20k1_hw_obj(hw_t *hw)
{
	if (hw->io_base) {
		hw_card_shutdown(hw);
	}
	kfree(container_of(hw, struct hw20k1, hw));
	return 0;
}
