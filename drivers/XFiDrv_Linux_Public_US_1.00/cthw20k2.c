/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	cthw20k2.c
 * 
 * @Brief   
 * This file contains the implementation of hardware access methord for 20k2.
 *
 * @Author	Liu Chun
 * @Date 	May 14 2008
 * 
 */

#include "cthardware.h"
#include "ctutils.h"
#include "ct20k2reg.h"
#include <linux/types.h>
#include <linux/slab.h>
#include <linux/pci.h>
#include <asm/io.h>
#include <linux/string.h>
#include <linux/kernel.h>
#include <linux/interrupt.h>

static u32 hw_read_20kx(struct hw *hw, u32 reg);	
static void hw_write_20kx(struct hw *hw, u32 reg, u32 data);

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
		u32 ca		: 28; /* (0:27) */
		u32 rsv		: 1;  /* (28:28) */
		u32 rs		: 3;  /* (29:31) */
	} bf;
	u32 data;
} srcca_t;

typedef union {
	struct {
		u32 sa		: 28; /* (0:27) */
		u32 rsv		: 4;  /* (28:31) */
	} bf;
	u32 data;
} srcsa_t;

typedef union {
	struct {
		u32 la		: 28; /* (0:27) */
		u32 rsv		: 4;  /* (28:31) */
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
			hw_write_20kx(hw, SRC_UPZ+idx*0x100+i*0x4, 0);
		}
		for (i = 0; i < 4; i++) {
			hw_write_20kx(hw, SRC_DN0Z+idx*0x100+i*0x4, 0);
		}
		for (i = 0; i < 8; i++) {
			hw_write_20kx(hw, SRC_DN1Z+idx*0x100+i*0x4, 0);
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
		hw_write_20kx(hw, MIXER_PRING_LO_HI+4*pm_idx, ctl->mpr.data);
		hw_write_20kx(hw, MIXER_PMOPLO+8*pm_idx, 0x3);
		hw_write_20kx(hw, MIXER_PMOPHI+8*pm_idx, 0x0);
		ctl->dirty.bf.mpr = 0;
	}
	if (ctl->dirty.bf.sa) {
		CTDPF("%s(%d) Writing srcsa = 0x%x\n", __func__, __LINE__, ctl->sa.data);
		hw_write_20kx(hw, SRC_SA+idx*0x100, ctl->sa.data);
		ctl->dirty.bf.sa = 0;
	}
	if (ctl->dirty.bf.la) {
		CTDPF("%s(%d) Writing srcla = 0x%x\n", __func__, __LINE__, ctl->la.data);
		hw_write_20kx(hw, SRC_LA+idx*0x100, ctl->la.data); 
		ctl->dirty.bf.la = 0;
	}
	if (ctl->dirty.bf.ca) {
		CTDPF("%s(%d) Writing srcca = 0x%x\n", __func__, __LINE__, ctl->ca.data);
		hw_write_20kx(hw, SRC_CA+idx*0x100, ctl->ca.data);
		ctl->dirty.bf.ca = 0;
	}

	/* Write srccf register */
	hw_write_20kx(hw, SRC_CF+idx*0x100, 0x0);

	if (ctl->dirty.bf.ccr) {
		CTDPF("%s(%d) Writing srcccr = 0x%x\n", __func__, __LINE__, ctl->ccr.data);
		hw_write_20kx(hw, SRC_CCR+idx*0x100, ctl->ccr.data);
		ctl->dirty.bf.ccr = 0;
	}
	if (ctl->dirty.bf.ctl) {
		CTDPF("%s(%d) Writing srcctl = 0x%x\n", __func__, __LINE__, ctl->ctl.data);
		hw_write_20kx(hw, SRC_CTL+idx*0x100, ctl->ctl.data);
		ctl->dirty.bf.ctl = 0;
	}

	return 0;
}

static int src_get_ca(struct hw *hw, unsigned int idx, void *blk)
{
	src_rsc_ctrl_blk_t *ctl = (src_rsc_ctrl_blk_t*)blk;

	/*CTDPF("%s(%d) Reading srcca = 0x%x\n", __func__, __LINE__, ctl->ca.data);*/
	ctl->ca.data = hw_read_20kx(hw, SRC_CA+idx*0x100);
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
	((src_mgr_ctrl_blk_t*)blk)->enbsa.data |= (0x1 << ((idx%128)/4));
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
		while (hw_read_20kx(hw, SRC_ENBSTAT) & 0x1);
		CTDPF("%s(%d) Writing enbsa = 0x%x\n", __func__, __LINE__, ctl->enbsa.data);
		hw_write_20kx(hw, SRC_ENBSA, ctl->enbsa.data);
		ctl->dirty.bf.enbsa = 0;
	}
	for (i = 0; i < 8; i++) {
		if ((ctl->dirty.data & (0x1 << i))) {
			CTDPF("%s(%d) Writing enb[%d] = 0x%x\n", __func__, __LINE__, i, ctl->enb[i].data);
			hw_write_20kx(hw, SRC_ENB + (i * 0x100), ctl->enb[i].data);
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
		hw_write_20kx(hw, SRC_IMAP+ctl->srcimap.idx*0x100, 
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
		u32 iv		: 1;  /* (2:2) */
		u32 rsv		: 1;  /* (3:3) */
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
	((amixer_rsc_ctrl_blk_t*)blk)->amoplo.bf.iv = iv;
	((amixer_rsc_ctrl_blk_t*)blk)->dirty.bf.amoplo = 1;
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
		hw_write_20kx(hw, MIXER_AMOPLO+idx*8, ctl->amoplo.data); 
		ctl->dirty.bf.amoplo = 0;
		CTDPF("%s(%d) Writing amophi = 0x%x\n", __func__, __LINE__, ctl->amophi.data);
		hw_write_20kx(hw, MIXER_AMOPHI+idx*8, ctl->amophi.data); 
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
		u32 srco	: 8;  /* (0:7) */
		u32 srcm	: 8;  /* (8:15) */
		u32 rsr		: 2;  /* (16:17) */
		u32 rsv1	: 2;  /* (18:19) */
		u32 drat	: 2;  /* (20:21) */
		u32 rsv2	: 2;  /* (22:23) */
		u32 ec		: 1;  /* (24:24) */
		u32 rsv3	: 3;  /* (25:27) */
		u32 et		: 1;  /* (28:28) */
		u32 rsv4	: 3;  /* (29:31) */
	} bf;
	u32 data;
} srt_t;

/* DAIO Receiver register dirty flags */
typedef union {
	struct {
		u16 srt		: 1;
		u16 rsv		: 15;
	} bf;
	u16 data;
} dai_dirty_t;

/* DAIO Receiver control block */
typedef struct dai_ctrl_blk {
	srt_t		srt;
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

/* Audio Transmitter Control and Status register */
typedef union {
	struct {
		u32 en		: 1;  /* (0:0) */
		u32 rsv		: 31; /* (1:31) */
	} bf;
	u32 data;
} atxctl_t;

/* Audio Receiver Control register */
typedef union {
	struct {
		u32 en		: 1;  /* (0:0) */
		u32 rsv		: 31; /* (1:31) */
	} bf;
	u32 data;
} arxctl_t;

/* DAIO manager register dirty flags */
typedef union {
	struct {
		u32 atxctl	: 8;
		u32 arxctl	: 8;
		u32 daoimap	: 1;
		u32 rsv		: 15;
	} bf;
	u32 data;
} daio_mgr_dirty_t;

/* DAIO manager control block */
typedef struct daio_mgr_ctrl_blk {
	daoimap_t		daoimap;
	atxctl_t		txctl[8];
	arxctl_t		rxctl[8];
	daio_mgr_dirty_t	dirty;
} daio_mgr_ctrl_blk_t;

static int dai_srt_set_srco(void *blk, unsigned int src)
{
	((dai_ctrl_blk_t*)blk)->srt.bf.srco = src;
	((dai_ctrl_blk_t*)blk)->dirty.bf.srt = 1;
	return 0;
}

static int dai_srt_set_srcm(void *blk, unsigned int src)
{
	((dai_ctrl_blk_t*)blk)->srt.bf.srcm = src;
	((dai_ctrl_blk_t*)blk)->dirty.bf.srt = 1;
	return 0;
}

static int dai_srt_set_rsr(void *blk, unsigned int rsr)
{
	((dai_ctrl_blk_t*)blk)->srt.bf.rsr = rsr;
	((dai_ctrl_blk_t*)blk)->dirty.bf.srt = 1;
	return 0;
}

static int dai_srt_set_drat(void *blk, unsigned int drat)
{
	((dai_ctrl_blk_t*)blk)->srt.bf.drat = drat;
	((dai_ctrl_blk_t*)blk)->dirty.bf.srt = 1;
	return 0;
}

static int dai_srt_set_ec(void *blk, unsigned int ec)
{
	((dai_ctrl_blk_t*)blk)->srt.bf.ec = (ec) ? 1 : 0;
	((dai_ctrl_blk_t*)blk)->dirty.bf.srt = 1;
	return 0;
}

static int dai_srt_set_et(void *blk, unsigned int et)
{
	((dai_ctrl_blk_t*)blk)->srt.bf.et = (et) ? 1 : 0;
	((dai_ctrl_blk_t*)blk)->dirty.bf.srt = 1;
	return 0;
}

static int dai_commit_write(struct hw *hw, unsigned int idx, void *blk)
{
	dai_ctrl_blk_t *ctl = blk;

	CTDPF("%s(%d) is called idx = 0x%x\n", __func__, __LINE__, idx);

	if (ctl->dirty.bf.srt) {
		CTDPF("%s(%d) Writing RX_SRT_CTL = 0x%x\n", __func__, __LINE__, ctl->srt.data);
		hw_write_20kx(hw, AUDIO_IO_RX_SRT_CTL+0x40*idx, ctl->srt.data); 
		ctl->dirty.bf.srt = 0;
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
	((daio_mgr_ctrl_blk_t*)blk)->rxctl[idx].bf.en = 1;
	((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.arxctl |= (0x1 << idx);
	return 0;
}

static int daio_mgr_dsb_dai(void *blk, unsigned int idx)
{
	((daio_mgr_ctrl_blk_t*)blk)->rxctl[idx].bf.en = 0;
	((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.arxctl |= (0x1 << idx);
	return 0;
}

static int daio_mgr_enb_dao(void *blk, unsigned int idx)
{
	((daio_mgr_ctrl_blk_t*)blk)->txctl[idx].bf.en = 1;
	((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.atxctl |= (0x1 << idx);
	return 0;
}

static int daio_mgr_dsb_dao(void *blk, unsigned int idx)
{
	((daio_mgr_ctrl_blk_t*)blk)->txctl[idx].bf.en = 0;
	((daio_mgr_ctrl_blk_t*)blk)->dirty.bf.atxctl |= (0x1 << idx);
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

	for (i = 0; i < 8; i++) {
		if ((ctl->dirty.bf.atxctl & (0x1 << i))) {
			data = hw_read_20kx(hw, (AUDIO_IO_TX_CTL+(0x40*i)));
			((atxctl_t*)&data)->bf.en = ctl->txctl[i].bf.en;
			CTDPF("%s(%d) Writing atxctl[%d] = 0x%x\n", __func__, __LINE__, i, data);
			hw_write_20kx(hw, (AUDIO_IO_TX_CTL+(0x40*i)), data);
			ctl->dirty.bf.atxctl &= ~(0x1 << i);
		}
		if ((ctl->dirty.bf.arxctl & (0x1 << i))) {
			data = hw_read_20kx(hw, (AUDIO_IO_RX_CTL+(0x40*i)));
			((arxctl_t*)&data)->bf.en = ctl->rxctl[i].bf.en;
			CTDPF("%s(%d) Writing arxctl[%d] = 0x%x\n", __func__, __LINE__, i, data);
			hw_write_20kx(hw, (AUDIO_IO_RX_CTL+(0x40*i)), data);
			ctl->dirty.bf.arxctl &= ~(0x1 << i);
		}
	}
	if (ctl->dirty.bf.daoimap) {
		CTDPF("%s(%d) Writing aim(%d) = 0x%x\n", __func__, __LINE__, ctl->daoimap.idx, ctl->daoimap.aim.data);
		hw_write_20kx(hw, AUDIO_IO_AIM+ctl->daoimap.idx*4, ctl->daoimap.aim.data); 
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
	u32 dwData = 0;
	int i;

	CTDPF("%s is called\n", __func__);
	CTASSERT(NULL != hw);
	CTASSERT(NULL != info);

	CTDPF("%s: msr = %d\n", __func__, info->msr);

	// Program I2S with proper sample rate and enable the correct I2S channel.
	// ED(0/8/16/24): Enable all I2S/I2X master clock output
	if (1 == info->msr) {
		hw_write_20kx(hw, AUDIO_IO_MCLK, 0x01010101);
		hw_write_20kx(hw, AUDIO_IO_TX_BLRCLK, 0x01010101);
		hw_write_20kx(hw, AUDIO_IO_RX_BLRCLK, 0);
	} else if (2 == info->msr) {
		hw_write_20kx(hw, AUDIO_IO_MCLK, 0x11111111);
		// Specify all playing 96khz
		// EA [0]	  - Enabled
		// RTA [4:5]   - 96kHz
		// EB [8]	  - Enabled
		// RTB [12:13] - 96kHz
		// EC [16]	 - Enabled
		// RTC [20:21] - 96kHz
		// ED [24]	 - Enabled
		// RTD [28:29] - 96kHz
		hw_write_20kx(hw, AUDIO_IO_TX_BLRCLK, 0x11111111);
		hw_write_20kx(hw, AUDIO_IO_RX_BLRCLK, 0);
	} else {
		printk(KERN_ALERT "ERROR!!! Invalid sampling rate!!!\n");
		return -EINVAL;
	}

	for(i=0; i<8; i++)
	{
		if(i <= 3) // 1st 3 channels are SPDIFs (SB0960)
		{
			if(i == 3)
			{
				dwData = 0x1001001;
			}
			else
			{
				dwData = 0x1000001;
			}

			hw_write_20kx(hw, (AUDIO_IO_TX_CTL + (0x40 * i)), dwData);
			hw_write_20kx(hw, (AUDIO_IO_RX_CTL + (0x40 * i)), dwData);

			// Initialize the SPDIF Out Channel status registers.  The value specified here
			// is based on the typical values provided in the specification, namely:
			// Clock Accuracy of 1000ppm, Sample Rate of 48KHz, unspecified source number,
			// Generation status = 1, Category code = 0x12 (Digital Signal Mixer), Mode = 0,
			// Emph = 0, Copy Permitted, AN = 0 (indicating that we're transmitting digital
			// audio, and the Professional Use bit is 0.
			// From CrmIONode.cpp

			hw_write_20kx(hw, (AUDIO_IO_TX_CSTAT_L + (0x40 * i)), 0x02109204);  // Default to 48kHz

			// Bit   32
			//		0   Maximum audio sample word length is 20 bits
			//		1   Maximum audio sample word length is 24 bits
			// Bit   33 34 35  Sample word length 
			//				 Audio sample word length if	 Audio sample word length if 
			//				 maximum length is 24 bits	   maximum length is 20 bits
			//				 as indicated by bit 32		  as indicated by bit 32
			// State 0  0  0   Word length not indicated	   Word length not indicated
			//				 (default)					   (default)
			//	   1  0  0	20 bits						 16 bits
			//	   0  1  0	22 bits						 18 bits
			//	   0  0  1	23 bits						 19 bits
			//	   1  0  1	24 bits						 20 bits
			//	   0  1  1	21 bits						 17 bits
			// All other combinations are reserved and shall not be used until further defined.
			// Currently set to 0xb...
			// From CrmIONode.cpp

			hw_write_20kx(hw, (AUDIO_IO_TX_CSTAT_H + (0x40 * i)), 0x0B);
		}
		else	// Next 5 channels are I2S (SB0960)
		{
			// Progarm I2S transmiter/receiver control register
			// EN(0): Transmitter/Receiver audio output/input enabled
			// MODE(4): Transmitter/Receiver mode is I2S
			dwData = 0x11;
			hw_write_20kx(hw, (AUDIO_IO_RX_CTL + (0x40 * i)), dwData);
			if(2 == info->msr)
			{
				// NUC [13:12] - Four channels per sample period
				dwData |= 0x1000;
			}
			hw_write_20kx(hw, (AUDIO_IO_TX_CTL + (0x40 * i)), dwData);
		}
	}

	return 0;
}

/* TRANSPORT operations */
static int hw_trn_init(struct hw *hw, const struct trn_conf *info)
{
	u32 vmctl = 0, data = 0;
	unsigned long ptp_phys_low = 0, ptp_phys_high = 0;
	int i = 0;

	CTDPF("%s is called\n", __func__);
	CTASSERT(NULL != hw);
	CTASSERT(NULL != info);
	
	/* Set up device page table */
	if ((~0UL) == info->vm_pgt_phys) {
		CTDPF("%s: Wrong device page table page addr!", __func__);
		return -1;
	}

	vmctl = 0x80000C0F;  /* 32-bit, 4k-size page */
#if BITS_PER_LONG == 64
	ptp_phys_low = info->vm_pgt_phys & ((1UL<<32)-1);
	ptp_phys_high = (info->vm_pgt_phys>>32) & ((1UL<<32)-1);
	vmctl |= (3<<8);
#elif BITS_PER_LONG == 32
	ptp_phys_low = info->vm_pgt_phys & (~0UL);
	ptp_phys_high = 0;
#else
#	error "Unknown BITS_PER_LONG!"
#endif
#if PAGE_SIZE == 8192
#	error "Don't support 8k-page!"
#endif
	CTDPF("%s: ptp_phys_low = 0x%p, ptp_phys_high = 0x%p, trnctl = 0x%x\n", __func__, ptp_phys_low, ptp_phys_high, vmctl);
	/* Write page table physical address to all PTPAL registers */
	for (i = 0; i < 64; i++) {
		hw_write_20kx(hw, VMEM_PTPAL+(16*i), ptp_phys_low);
		hw_write_20kx(hw, VMEM_PTPAH+(16*i), ptp_phys_high);
	}
	/* Enable virtual memory transfer */
	hw_write_20kx(hw, VMEM_CTL, vmctl);
	/* Enable transport bus master and queueing of request */
	hw_write_20kx(hw, TRANSPORT_CTL, 0x03);
	hw_write_20kx(hw, TRANSPORT_INT, 0x200c01);
	/* Enable transport ring */
	data = hw_read_20kx(hw, TRANSPORT_ENB);
	hw_write_20kx(hw, TRANSPORT_ENB, (data | 0x03));

	return 0;
}

/* Card initialization */
typedef union {
	struct {
		u16 aie		: 1;  /* (0:0) */
		u16 uaa		: 1;  /* (1:1) */
		u16 dpc		: 1;  /* (2:2) */
		u16 dbp		: 1;  /* (3:3) */
		u16 abp		: 1;  /* (4:4) */
		u16 tbp		: 1;  /* (5:5) */
		u16 sbp		: 1;  /* (6:6) */
		u16 fbp		: 1;  /* (7:7) */
		u16 me		: 1;  /* (8:8) */
		u16 rsv1	: 3;  /* (9:11) */
		u16 aid		: 1;  /* (12:12) */
		u16 rsv2	: 3;  /* (13:15) */
	} bf;
	u16 data;
} gctl_t;

typedef union {
	struct {
		u32 clk		: 1;  /* (0:0) */
		u32 arc		: 1;  /* (1:1) */
		u32 uaa		: 1;  /* (2:2) */
		u32 d3pd	: 1;  /* (3:3) */
		u32 rsv		: 28; /* (4:31) */
	} bf;
	u32 data;
} pllenb_t;

typedef union {
	struct {
		u32 src		: 3;  /* (0:2) */
		u32 spe		: 1;  /* (3:3) */
		u32 rd		: 4;  /* (4:7) */
		u32 fd		: 9;  /* (8:16) */
		u32 od		: 2;  /* (17:18) */
		u32 b		: 1;  /* (19:19) */
		u32 as		: 1;  /* (20:20) */
		u32 lf		: 5;  /* (21:25) */
		u32 sps		: 3;  /* (25:28) */
		u32 ad		: 2;  /* (29:30) */
		u32 rsv		: 1;  /* (31:31) */
	} bf;
	u32 data;
} pllctl_t;

typedef union {
	struct {
		u32 ccs		: 3;  /* (0:2) */
		u32 spl		: 1;  /* (3:3) */
		u32 crd		: 4;  /* (4:7) */
		u32 cfd		: 9;  /* (8:16) */
		u32 sl		: 1;  /* (17:17) */
		u32 fas		: 1;  /* (18:18) */
		u32 b		: 1;  /* (19:19) */
		u32 pd		: 1;  /* (20:20) */
		u32 oca		: 1;  /* (21:21) */
		u32 nca		: 1;  /* (22:22) */
		u32 rsv		: 9;  /* (23:31) */
	} bf;
	u32 data;
} pllstat_t;

typedef union {
	u32 data;
} gpioctl_t;

static int hw_pll_init(struct hw *hw, unsigned int rsr)
{
	pllenb_t pllenb;
	pllctl_t pllctl;
	pllstat_t pllstat;
	int i;

	pllenb.data = 0xB;
	hw_write_20kx(hw, PLL_ENB, pllenb.data);
	pllctl.data = 0x20D00000;
	pllctl.bf.fd = 16 - 4;
	hw_write_20kx(hw, PLL_CTL, pllctl.data);
	mdelay(40);
	pllctl.data = hw_read_20kx(hw, PLL_CTL);
	CTDPF("%s: pllctl read = 0x%x\n", __func__, pllctl.data);
	pllctl.bf.b = 0;
	if (48000 == rsr) {
		pllctl.bf.fd = 16 - 2;
		pllctl.bf.rd = 0;
	} else { /* 44100 */
		pllctl.bf.fd = 294 - 2;
		pllctl.bf.rd = 3;
	}
	CTDPF("%s: pllctl to write = 0x%x\n", __func__, pllctl.data);
	hw_write_20kx(hw, PLL_CTL, pllctl.data);
	mdelay(40);
	for (i = 0; i < 1000; i++) {
		pllstat.data = hw_read_20kx(hw, PLL_STAT);
		if (pllstat.bf.pd) {
			continue;
		}
		if (pllstat.bf.b != pllctl.bf.b) {
			continue;
		}
		if (pllstat.bf.ccs != pllctl.bf.src) {
			continue;
		}
		if (pllstat.bf.crd != pllctl.bf.rd) {
			continue;
		}
		if (pllstat.bf.cfd != pllctl.bf.fd) {
			continue;
		}
		break;
	}
	if (i >= 1000) {
		printk(KERN_ALERT "PLL initialization failed!!!\n");
		return -EBUSY;
	}

	return 0;
}

static int hw_auto_init(struct hw *hw)
{
	gctl_t gctl; 
	int i;

	gctl.data = hw_read_20kx(hw, GLOBAL_CNTL_GCTL);
	CTDPF("%s: gctl = 0x%x\n", __func__, gctl.data);
	gctl.bf.aie = 0;
	hw_write_20kx(hw, GLOBAL_CNTL_GCTL, gctl.data);
	gctl.bf.aie = 1;
	hw_write_20kx(hw, GLOBAL_CNTL_GCTL, gctl.data);
	mdelay(10);
	for (i = 0; i < 400000; i++) {
		gctl.data = hw_read_20kx(hw, GLOBAL_CNTL_GCTL);
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

/* DAC operations */

#define CS4382_MC1 		0x1
#define CS4382_MC2 		0x2
#define CS4382_MC3		0x3
#define CS4382_FC		0x4
#define CS4382_IC		0x5
#define CS4382_XC1		0x6
#define CS4382_VCA1 		0x7
#define CS4382_VCB1 		0x8	
#define CS4382_XC2		0x9
#define CS4382_VCA2 		0xA
#define CS4382_VCB2 		0xB
#define CS4382_XC3		0xC
#define CS4382_VCA3		0xD
#define CS4382_VCB3		0xE
#define CS4382_XC4 		0xF
#define CS4382_VCA4 		0x10
#define CS4382_VCB4 		0x11
#define CS4382_CREV 		0x12

// I2C status
#define STATE_LOCKED		0x00
#define STATE_UNLOCKED		0xAA
#define DATA_READY		0x800000    // Used with I2C_IF_STATUS
#define DATA_ABORT		0x10000     // Used with I2C_IF_STATUS

typedef union
{
	struct
	{	//	 Mnemonic		  lo - hi   Function
		u32 dcm	 :  1; //< 0 -  0   Direct_Control_Mode  - In Direct Control Mode, please set the controller to full access mode by sending the "full_access" keys.
		u32 bc	  :  2; //< 1 -  2   Byte_Count		   - 
		u32 apd	 :  1; //< 3 -  3   Ack_Poll_Disable	 - For hardware debugging only. Default '0'.
		u32 pad0	: 12; //< 4 - 15   Reserved			
		u32 ab	  :  1; //<16 - 16   Abort				- Write '1' to clear this status bit.
		u32 pad1	:  6; //<17 - 22   Reserved			
		u32 dr	  :  1; //<23 - 23   Data_Ready		   - When set indicate data is ready to be read/written from/to the I2C.
	}_bf;

	u32 dwWord;
} CRM_I2C_STATUS;

typedef union
{
	struct
	{	//	 Mnemonic		  lo - hi   Function
		u32 ptad	: 16; ///< 0 - 15   Pointer_Addr		 - I2C Pointer Address (Byte Address). Used in I2C Mode. Ignored (not transmitted out) in Direct Control Mode.
		u32 slad	:  7; ///<16 - 22   Slave_Addr		   - 0x57 (Do not change this when I2C Interface is in I2C mode. 0x57 is the fixed address of the I2C ). This field is used to "chip select" (i.e. to address) an I2C device.
	}_bf;

	u32 dwWord;
} CRM_I2C_ADDRESS;

typedef struct _tagCS4382_RegsCache
{
	u32 dwModeControl_1;
	u32 dwModeControl_2;
	u32 dwModeControl_3;

	u32 dwFilterControl;
	u32 dwInvertControl;

	u32 dwMixControl_P1;
	u32 dwVolControl_A1;
	u32 dwVolControl_B1;

	u32 dwMixControl_P2;
	u32 dwVolControl_A2;
	u32 dwVolControl_B2;

	u32 dwMixControl_P3;
	u32 dwVolControl_A3;
	u32 dwVolControl_B3;

	u32 dwMixControl_P4;
	u32 dwVolControl_A4;
	u32 dwVolControl_B4;
} REGS_CS4382;

static u8 m_bAddressSize, m_bDataSize, m_bDeviceID;

static int I2CUnlockFullAccess(struct hw *hw)
{
	u8 UnlockKeySequence_FLASH_FULLACCESS_MODE[2] =  {0xB3, 0xD4};

	// Send keys for forced BIOS mode
	hw_write_20kx(hw, I2C_IF_WLOCK, UnlockKeySequence_FLASH_FULLACCESS_MODE[0]);
	hw_write_20kx(hw, I2C_IF_WLOCK, UnlockKeySequence_FLASH_FULLACCESS_MODE[1]);
	// Check whether the chip is unlocked
	if(hw_read_20kx(hw, I2C_IF_WLOCK) == STATE_UNLOCKED) {
		return 0;
	}
	return -1;
}

static int I2CLockChip(struct hw *hw)
{
	// Write twice
	hw_write_20kx(hw, I2C_IF_WLOCK, STATE_LOCKED);
	hw_write_20kx(hw, I2C_IF_WLOCK, STATE_LOCKED);
	if(hw_read_20kx(hw, I2C_IF_WLOCK) == STATE_LOCKED) {
		return 0;
	}

	return -1;
}

static int I2CInit(struct hw *hw, u8 bDeviceID, u8 bAddressSize, u8 bDataSize)
{
	int err = 0;
	CRM_I2C_STATUS RegI2CStatus;
	CRM_I2C_ADDRESS RegI2CAddress;

	CTDPF("%s: line %d\n", __func__, __LINE__);
	if((err = I2CUnlockFullAccess(hw)) < 0) {
		return err;
	}

	CTDPF("%s: line %d\n", __func__, __LINE__);
	m_bAddressSize = bAddressSize;
	m_bDataSize = bDataSize;
	m_bDeviceID = bDeviceID;

	RegI2CAddress.dwWord = 0;
	RegI2CAddress._bf.slad = bDeviceID;

	CTDPF("%s: line %d RegI2CAddress = 0x%x\n", __func__, __LINE__, RegI2CAddress.dwWord);
	hw_write_20kx(hw, I2C_IF_ADDRESS, RegI2CAddress.dwWord);

	RegI2CStatus.dwWord = hw_read_20kx(hw, I2C_IF_STATUS);

	//RegI2CStatus._bf.bc = (4 == (bAddressSize + bDataSize))? 0 : (bAddressSize + bDataSize);
	//RegI2CStatus._bf.bc = 0;	 // 20k2 memory can only access in dword
	RegI2CStatus._bf.dcm = 1;	// Direct control mode

	hw_write_20kx(hw, I2C_IF_STATUS, RegI2CStatus.dwWord);

	return 0;
}

static int I2CUninit(struct hw *hw)
{
	CRM_I2C_STATUS RegI2CStatus;
	CRM_I2C_ADDRESS RegI2CAddress;

	RegI2CAddress.dwWord = 0;
	RegI2CAddress._bf.slad = 0x57;   // I2C id

	hw_write_20kx(hw, I2C_IF_ADDRESS, RegI2CAddress.dwWord);

	RegI2CStatus.dwWord = hw_read_20kx(hw, I2C_IF_STATUS);

	RegI2CStatus._bf.dcm = 0;	// I2C mode

	hw_write_20kx(hw, I2C_IF_STATUS, RegI2CStatus.dwWord);

	return I2CLockChip(hw);
}

static int I2CWaitDataReady(struct hw *hw)
{
	int i = 0x400000;
	while((!(hw_read_20kx(hw, I2C_IF_STATUS) & DATA_READY)) && --i);
	CTDPF("%s: line %d I2C_IF_STATUS = 0x%x i = %d\n", __func__, __LINE__, hw_read_20kx(hw, I2C_IF_STATUS), i);
	return i;
}

static int I2CRead(struct hw *hw, u16 wAddress, u32* pdwData)
{
	CRM_I2C_STATUS RegI2CStatus;

	CTDPF("%s: line %d\n", __func__, __LINE__);
	RegI2CStatus.dwWord = hw_read_20kx(hw, I2C_IF_STATUS);
	RegI2CStatus._bf.bc = (4 == m_bAddressSize)? 0 : m_bAddressSize;
	hw_write_20kx(hw, I2C_IF_STATUS, RegI2CStatus.dwWord);
	if (!I2CWaitDataReady(hw)) {
		return -1;
	}
	hw_write_20kx(hw, I2C_IF_WDATA, (u32)wAddress);
	if (!I2CWaitDataReady(hw)) {
		return -1;
	}
	// Force a read operation
	hw_write_20kx(hw, I2C_IF_RDATA, 0);
	if (!I2CWaitDataReady(hw)) {
		return -1;
	}
	*pdwData = hw_read_20kx(hw, I2C_IF_RDATA);
	return 0;
}

static int I2CWrite(struct hw *hw, u16 wAddress, u32 dwData)
{
	u32 dwI2CData = (dwData << (m_bAddressSize * 8)) | wAddress;
	CRM_I2C_STATUS RegI2CStatus;

	CTDPF("%s: line %d\n", __func__, __LINE__);
	RegI2CStatus.dwWord = hw_read_20kx(hw, I2C_IF_STATUS);

	RegI2CStatus._bf.bc = (4 == (m_bAddressSize + m_bDataSize))? 0 : (m_bAddressSize + m_bDataSize);

	hw_write_20kx(hw, I2C_IF_STATUS, RegI2CStatus.dwWord);
	I2CWaitDataReady(hw);
	// Dummy write to trigger the write oprtation
	hw_write_20kx(hw, I2C_IF_WDATA, 0);
	I2CWaitDataReady(hw);

	// This is the real data
	hw_write_20kx(hw, I2C_IF_WDATA, dwI2CData);
	I2CWaitDataReady(hw);

	return 0;
}

static int hw_dac_init(struct hw *hw, const struct dac_conf *info)
{
	int err = 0;
	u32 dwData = 0;
	int i=0;
	REGS_CS4382 cs4382_Read = {0};
	REGS_CS4382 cs4382_Def = { 0x00000001,  // Mode Control 1
				   0x00000000,  // Mode Control 2
				   0x00000084,  // Mode Control 3
				   0x00000000,  // Filter Control
				   0x00000000,  // Invert Control
				   0x00000024,  // Mixing Control Pair 1
				   0x00000000,  // Vol Control A1
				   0x00000000,  // Vol Control B1
				   0x00000024,  // Mixing Control Pair 2
				   0x00000000,  // Vol Control A2
				   0x00000000,  // Vol Control B2
				   0x00000024,  // Mixing Control Pair 3
				   0x00000000,  // Vol Control A3
				   0x00000000,  // Vol Control B3
				   0x00000024,  // Mixing Control Pair 4
				   0x00000000,  // Vol Control A4
				   0x00000000   // Vol Control B4
				 };

	CTDPF("%s is called\n", __func__);
	CTASSERT(NULL != hw);
	CTASSERT(NULL != info);

	/* Set DAC reset bit as output */
	dwData = hw_read_20kx(hw, GPIO_CTRL);
	CTDPF("%s: line %d dwData = 0x%x\n", __func__, __LINE__, dwData);
	dwData |= 0x02;
	hw_write_20kx(hw, GPIO_CTRL, dwData);

	if ((err = I2CInit(hw, 0x18, 1, 1)) < 0) {
		goto End;
	}

	for(i=0; i<2; i++) {
		// Reset DAC twice just in-case the chip didn't initialized properly
		dwData = hw_read_20kx(hw, GPIO_DATA);
		CTDPF("%s: line %d dwData = 0x%x\n", __func__, __LINE__, dwData);
		// GPIO data bit 1
		dwData &= 0xFFFFFFFD;
		hw_write_20kx(hw, GPIO_DATA, dwData);
		mdelay(10);
		dwData |= 0x2;
		hw_write_20kx(hw, GPIO_DATA, dwData);
		mdelay(50);	

		// Reset the 2nd time
		dwData &= 0xFFFFFFFD;
		hw_write_20kx(hw, GPIO_DATA, dwData);
		mdelay(10);
		dwData |= 0x2;
		hw_write_20kx(hw, GPIO_DATA, dwData);
		mdelay(50);

		CTDPF("%s: line %d dwData = 0x%x\n", __func__, __LINE__, hw_read_20kx(hw, GPIO_DATA));
		if (I2CRead(hw, CS4382_MC1,  &cs4382_Read.dwModeControl_1)) {
			continue;
		}
		if (I2CRead(hw, CS4382_MC2,  &cs4382_Read.dwModeControl_2)) {
			continue;
		}
		if (I2CRead(hw, CS4382_MC3,  &cs4382_Read.dwModeControl_3)) {
			continue;
		}
		if (I2CRead(hw, CS4382_FC,   &cs4382_Read.dwFilterControl)) {
			continue;
		}
		if (I2CRead(hw, CS4382_IC,   &cs4382_Read.dwInvertControl)) {
			continue;
		}
		if (I2CRead(hw, CS4382_XC1,  &cs4382_Read.dwMixControl_P1)) {
			continue;
		}
		if (I2CRead(hw, CS4382_VCA1, &cs4382_Read.dwVolControl_A1)) {
			continue;
		}
		if (I2CRead(hw, CS4382_VCB1, &cs4382_Read.dwVolControl_B1)) {
			continue;
		}
		if (I2CRead(hw, CS4382_XC2,  &cs4382_Read.dwMixControl_P2)) {
			continue;
		}
		if (I2CRead(hw, CS4382_VCA2, &cs4382_Read.dwVolControl_A2)) {
			continue;
		}
		if (I2CRead(hw, CS4382_VCB2, &cs4382_Read.dwVolControl_B2)) {
			continue;
		}
		if (I2CRead(hw, CS4382_XC3,  &cs4382_Read.dwMixControl_P3)) {
			continue;
		}
		if (I2CRead(hw, CS4382_VCA3, &cs4382_Read.dwVolControl_A3)) {
			continue;
		}
		if (I2CRead(hw, CS4382_VCB3, &cs4382_Read.dwVolControl_B3)) {
			continue;
		}
		if (I2CRead(hw, CS4382_XC4,  &cs4382_Read.dwMixControl_P4)) {
			continue;
		}
		if (I2CRead(hw, CS4382_VCA4, &cs4382_Read.dwVolControl_A4)) {
			continue;
		}
		if (I2CRead(hw, CS4382_VCB4, &cs4382_Read.dwVolControl_B4)) {
			continue;
		}
		if(memcmp(&cs4382_Read, &cs4382_Def, sizeof(REGS_CS4382)) != 0)
		{
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwModeControl_1, cs4382_Read.dwModeControl_1);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwModeControl_2, cs4382_Read.dwModeControl_2);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwModeControl_3, cs4382_Read.dwModeControl_3);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwFilterControl, cs4382_Read.dwFilterControl);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwInvertControl, cs4382_Read.dwInvertControl);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwMixControl_P1, cs4382_Read.dwMixControl_P1);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwVolControl_A1, cs4382_Read.dwVolControl_A1);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwVolControl_B1, cs4382_Read.dwVolControl_B1);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwMixControl_P2, cs4382_Read.dwMixControl_P2);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwVolControl_A2, cs4382_Read.dwVolControl_A2);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwVolControl_B2, cs4382_Read.dwVolControl_B2);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwMixControl_P3, cs4382_Read.dwMixControl_P3);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwVolControl_A3, cs4382_Read.dwVolControl_A3);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwVolControl_B3, cs4382_Read.dwVolControl_B3);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwMixControl_P4, cs4382_Read.dwMixControl_P4);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n", cs4382_Def.dwVolControl_A4, cs4382_Read.dwVolControl_A4);
			CTDPF(KERN_ALERT "0x%08X, 0x%08X\n\n", cs4382_Def.dwVolControl_B4, cs4382_Read.dwVolControl_B4);
			continue;
		}
		else
		{
			break;
		}
	}

	if(i >= 2)
	{
		goto End;
	}

	// Note: Every I2C write must have some delay. This is not a requirement but the delay works here...
	I2CWrite(hw, CS4382_MC1, 0x80);
	I2CWrite(hw, CS4382_MC2, 0x10);
	if(1 == info->msr)
	{
		I2CWrite(hw, CS4382_XC1, 0x24);
		I2CWrite(hw, CS4382_XC2, 0x24);
		I2CWrite(hw, CS4382_XC3, 0x24);
		I2CWrite(hw, CS4382_XC4, 0x24);
	}
	else if(2 == info->msr)
	{
		I2CWrite(hw, CS4382_XC1, 0x25);
		I2CWrite(hw, CS4382_XC2, 0x25);
		I2CWrite(hw, CS4382_XC3, 0x25);
		I2CWrite(hw, CS4382_XC4, 0x25);
	}
	else
	{
		I2CWrite(hw, CS4382_XC1, 0x26);
		I2CWrite(hw, CS4382_XC2, 0x26);
		I2CWrite(hw, CS4382_XC3, 0x26);
		I2CWrite(hw, CS4382_XC4, 0x26);
	}
#ifdef DEBUG
	dwData = hw_read_20kx(hw, GPIO_CTRL);
	CTDPF("%s(%d) GPIO_CTRL = 0x%x\n", __func__, __LINE__, dwData);
	dwData = hw_read_20kx(hw, GPIO_DATA);
	CTDPF("%s(%d) GPIO_DATA = 0x%x\n", __func__, __LINE__, dwData);
#endif
	return 0;
End:

	I2CUninit(hw);
	return -1;
}

/* ADC operations */
#define MAKE_WM8775_ADDR(addr,data)	(u32)(((addr<<1)&0xFE)|((data>>8)&0x1))
#define MAKE_WM8775_DATA(data)	(u32)(data&0xFF)

#define WM8775_IC       0x0B
#define WM8775_MMC      0x0C
#define WM8775_AADCL    0x0E
#define WM8775_AADCR    0x0F
#define WM8775_ADCMC    0x15
#define WM8775_RESET    0x17

static int hw_is_adc_input_selected(struct hw *hw, enum ADCSRC type)
{
	u32 data = 0;

	data = hw_read_20kx(hw, GPIO_DATA);
	switch (type) {
	case ADC_MICIN: 
		data = (data & (0x1 << 14)) ? 1 : 0;
		break;
	case ADC_LINEIN:
		data = (data & (0x1 << 14)) ? 0 : 1;
		break;
	default:
		data = 0;
	}
	return data;
}

static int hw_adc_input_select(struct hw *hw, enum ADCSRC type)
{
	u32 data = 0;

	data = hw_read_20kx(hw, GPIO_DATA);
	switch (type) {
	case ADC_MICIN: 
		data |= (0x1 << 14);
		hw_write_20kx(hw, GPIO_DATA, data);
		I2CWrite(hw, MAKE_WM8775_ADDR(WM8775_ADCMC, 0x101), 
						MAKE_WM8775_DATA(0x101)); /* Mic-in */
		I2CWrite(hw, MAKE_WM8775_ADDR(WM8775_AADCL, 0xE7),
						MAKE_WM8775_DATA(0xE7)); /* +12dB boost */
		I2CWrite(hw, MAKE_WM8775_ADDR(WM8775_AADCR, 0xE7), 
						MAKE_WM8775_DATA(0xE7)); /* +12dB boost */
		break;
	case ADC_LINEIN:
		data &= ~(0x1 << 14);
		hw_write_20kx(hw, GPIO_DATA, data);
		I2CWrite(hw, MAKE_WM8775_ADDR(WM8775_ADCMC, 0x102), 
						MAKE_WM8775_DATA(0x102)); /* Line-in */
		I2CWrite(hw, MAKE_WM8775_ADDR(WM8775_AADCL, 0xCF), 
						MAKE_WM8775_DATA(0xCF)); /* No boost */
		I2CWrite(hw, MAKE_WM8775_ADDR(WM8775_AADCR, 0xCF), 
						MAKE_WM8775_DATA(0xCF)); /* No boost */
		break;
	default:
		break;
	}

	return 0;
}

static int hw_adc_init(struct hw *hw, const struct adc_conf *info)
{
	int err = 0;
	u32 dwMux = 2, dwData = 0, dwCtl = 0;

	CTDPF("%s is called\n", __func__);
	CTASSERT(NULL != hw);
	CTASSERT(NULL != info);

	/*  Set ADC reset bit as output */
	dwData = hw_read_20kx(hw, GPIO_CTRL);
	dwData |= (0x1 << 15);
	hw_write_20kx(hw, GPIO_CTRL, dwData);

	/* Initialize I2C */
	if((err = I2CInit(hw, 0x1A, 1, 1)) < 0) {
		printk(KERN_ALERT "%s(%d) - Failure to acquire I2C!!!\n", __func__, __LINE__);
		goto error;
	}

	/* Make ADC in normal operation */
	dwData = hw_read_20kx(hw, GPIO_DATA);
	dwData &= ~(0x1 << 15);
	mdelay(10);
	dwData |= (0x1 << 15);
	hw_write_20kx(hw, GPIO_DATA, dwData);
	mdelay(50);	

	// Set the master mode (256fs)
	if(1 == info->msr)
	{
		I2CWrite(hw, MAKE_WM8775_ADDR(WM8775_MMC, 0x02), MAKE_WM8775_DATA(0x02));
	}
	else if(2 == info->msr)
	{
		I2CWrite(hw, MAKE_WM8775_ADDR(WM8775_MMC,0x0A), MAKE_WM8775_DATA(0x0A));
	}
	else
	{
		printk(KERN_ALERT "%s(%d) - Invalid master sampling rate %d!!!\n", __func__, __LINE__, info->msr);
		err = -EINVAL;
		goto error;
	}

	// Configure GPIO bit 14 change to line-in/mic-in
	dwCtl = hw_read_20kx(hw, GPIO_CTRL);
	dwCtl |= 0x1<<14;
	hw_write_20kx(hw, GPIO_CTRL, dwCtl);

	// Check using Mic-in or Line-in
	dwData = hw_read_20kx(hw, GPIO_DATA);

	if(dwMux == 1)  // Mic-in
	{
		// Configures GPIO data to select Mic-in
		dwData |= 0x1<<14;
		hw_write_20kx(hw, GPIO_DATA, dwData);

		I2CWrite(hw, MAKE_WM8775_ADDR(WM8775_ADCMC, 0x101), MAKE_WM8775_DATA(0x101));   // Mic-in
		I2CWrite(hw, MAKE_WM8775_ADDR(WM8775_AADCL, 0xE7), MAKE_WM8775_DATA(0xE7));	 // +12dB boost
		I2CWrite(hw, MAKE_WM8775_ADDR(WM8775_AADCR, 0xE7), MAKE_WM8775_DATA(0xE7));	 // +12dB boost
	}
	else if(dwMux == 2)   // Line-in
	{
		// Configures GPIO data to select Line-in
		dwData &= ~(0x1<<14);
		hw_write_20kx(hw, GPIO_DATA, dwData);

		// Setup ADC
		I2CWrite(hw, MAKE_WM8775_ADDR(WM8775_ADCMC, 0x102), MAKE_WM8775_DATA(0x102));   // Line-in
		I2CWrite(hw, MAKE_WM8775_ADDR(WM8775_AADCL, 0xCF), MAKE_WM8775_DATA(0xCF));	 // No boost
		I2CWrite(hw, MAKE_WM8775_ADDR(WM8775_AADCR, 0xCF), MAKE_WM8775_DATA(0xCF));	 // No boost
	}
	else if(dwMux == 4)  // Mic array
	{
		// Mic Array...
		// Configures GPIO data to select Mic-in
		//dwData |= 1<<14;
		//MemWriteDword(GPIO_DATA, dwData);

		//I2CWrite(MAKE_WM8775_ADDR(WM8775_ADCMC, 0x103), MAKE_WM8775_DATA(0x103));
		//I2CWrite(MAKE_WM8775_ADDR(WM8775_AADCL, 0xED), MAKE_WM8775_DATA(0xED));
		//I2CWrite(MAKE_WM8775_ADDR(WM8775_AADCR, 0xED), MAKE_WM8775_DATA(0xED));
	}
	else
	{
		printk(KERN_ALERT "%s(%d) - ERROR!!! Invalid input mux!!!\n", __func__, __LINE__);
		err = -EINVAL;
		goto error;
	}
#ifdef DEBUG
	dwData = hw_read_20kx(hw, GPIO_DATA);
	dwCtl = hw_read_20kx(hw, GPIO_CTRL);
	CTDPF("%s(%d) GPIO_CTRL = 0x%x GPIO_DATA = 0x%x\n", __func__, __LINE__, dwCtl, dwData);
#endif
	return 0;

error:
	I2CUninit(hw);
	return err;
}

static int hw_line_out_unmute(struct hw *hw, unsigned int state)
{
	u32 data = 0;

	data = hw_read_20kx(hw, GPIO_DATA);
	if (state) {
		hw_write_20kx(hw, GPIO_DATA, (data | (0x1 << 11)));
	} else {
		hw_write_20kx(hw, GPIO_DATA, (data & ~(0x1 << 11)));
	}

	return 0;
}

static int hw_have_digit_io_switch(struct hw *hw)
{
	return 0;
}

static int hw_card_start(struct hw *hw)
{
	int err = 0;
	struct pci_dev *pci = hw->pci;
	gctl_t gctl; 
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

	hw->io_base = pci_resource_start(hw->pci, 2);
	hw->mem_base = (unsigned long)ioremap(hw->io_base, 
					pci_resource_len(hw->pci, 2));
	if (NULL == (void *)hw->mem_base) {
		err = -ENOENT;
		goto error2;
	}

	/* Switch to 20k2 mode from UAA mode. */
	gctl.data = hw_read_20kx(hw, GLOBAL_CNTL_GCTL);
	gctl.bf.uaa = 0;
	CTDPF("%s: gctl = 0x%x\n", __func__, gctl.data);
	hw_write_20kx(hw, GLOBAL_CNTL_GCTL, gctl.data);

	/*if ((err = request_irq(pci->irq, ct_atc_interrupt, IRQF_SHARED, 
				atc->chip_details->nm_card, hw))) {
		goto error3;
	}
	hw->irq = pci->irq;
	*/

	pci_set_master(pci);

	return 0;

/*error3:
	iounmap((void *)hw->mem_base);
	hw->mem_base = (unsigned long)NULL;*/
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

	/* Get PCI io port/memory base address and do 20kx core switch if needed. */
	if (!hw->io_base) {
		if ((err = hw_card_start(hw))) {
			return err;
		}
	}
#ifdef DEBUG
	for (iter_count = 0; iter_count < 6; iter_count++) {
		CTDPF("%s: BAR %d start = 0x%x size = 0x%x flags = 0x%x\n", __func__, iter_count, pci_resource_start(hw->pci, iter_count), pci_resource_len(hw->pci, iter_count), pci_resource_flags(hw->pci, iter_count));
	}
#endif
	CTDPF("%s: io_base = 0x%x mem_base = 0x%lx\n", __func__, hw->io_base, hw->mem_base);

	/* PLL init */
	if ((err = hw_pll_init(hw, info->rsr)) < 0) {
		return err;
	}

	/* kick off auto-init */
	if ((err = hw_auto_init(hw)) < 0) {
		return err;
	}

	gctl.data = hw_read_20kx(hw, GLOBAL_CNTL_GCTL);
	gctl.bf.dbp = 1;
	gctl.bf.tbp = 1;
	gctl.bf.fbp = 1;
	gctl.bf.dpc = 0;
	hw_write_20kx(hw, GLOBAL_CNTL_GCTL, gctl.data);

	/* Reset all global pending interrupts */
	hw_write_20kx(hw, INTERRUPT_GIE, 0);
	/* Reset all SRC pending interrupts */
	hw_write_20kx(hw, SRC_IP, 0);

	/* TODO: detect the card ID and configure GPIO accordingly. */
	/* Configures GPIO (0xD802 0x98028) */
	/*hw_write_20kx(hw, GPIO_CTRL, 0x7F07);*/
	/* Configures GPIO (SB0880) */
	/*hw_write_20kx(hw, GPIO_CTRL, 0xFF07);*/
	hw_write_20kx(hw, GPIO_CTRL, 0xD802);

	/* Enable audio ring */
	hw_write_20kx(hw, MIXER_AR_ENABLE, 0x01);

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
	
	data = hw_read_20kx(hw, SRC_MCTL);
	data |= 0x1; /* Enables input from the audio ring */
	hw_write_20kx(hw, SRC_MCTL, data);

	return 0;
}

static u32 hw_read_20kx(struct hw *hw, u32 reg)
{
	return readl((void *)(hw->mem_base + reg));
}	

static void hw_write_20kx(struct hw *hw, u32 reg, u32 data)
{
	writel(data, (void *)(hw->mem_base + reg));
}

int create_20k2_hw_obj(hw_t **rhw)
{
	hw_t *hw;

	CTASSERT(NULL != rhw);
	*rhw = NULL;
	hw = kzalloc(sizeof(*hw), GFP_KERNEL);
	if (NULL == hw) {
		CTDPF("%s(%d): Memory allocation failed!!!", __func__, __LINE__);
		return -ENOMEM;
	}
	
	hw->io_base = 0;
	hw->mem_base = (unsigned long)NULL;
	hw->irq = -1;

	hw->card_init = hw_card_init;
	hw->card_stop = hw_card_stop;
	hw->is_adc_source_selected = hw_is_adc_input_selected;
	hw->select_adc_source = hw_adc_input_select;
	hw->line_out_unmute = hw_line_out_unmute;
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
	hw->dai_srt_set_srco = dai_srt_set_srco;
	hw->dai_srt_set_srcm = dai_srt_set_srcm;
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

int destroy_20k2_hw_obj(hw_t *hw)
{
	if (hw->io_base) {
		hw_card_shutdown(hw);
	}
	kfree(hw);
	return 0;
}
