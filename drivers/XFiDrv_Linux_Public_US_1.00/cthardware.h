/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	cthardware.h
 * 
 * @Brief   
 * This file contains the definition of hardware access methord.
 *
 * @Author	Liu Chun
 * @Date 	May 13 2008
 * 
 */

#ifndef CTHARDWARE_H
#define CTHARDWARE_H

#include <linux/types.h>
#include <linux/pci.h>

enum CHIPTYP {
	ATC20K1,
	ATC20K2,
	ATCNONE
};

/* Type of input source for ADC */
enum ADCSRC{
	ADC_MICIN,
	ADC_LINEIN,
	ADC_VIDEO,
	ADC_AUX,
	ADC_NONE	/* Switch to digital input */
};

struct card_conf {
	/* device virtual mem page table page physical addr 
	 * (supporting one page table page now) */
	unsigned long vm_pgt_phys; 
	unsigned int rsr;	/* reference sample rate in Hzs*/
	unsigned int msr;	/* master sample rate in rsrs */
};

typedef struct hw {
	int (*card_init)(struct hw *hw, struct card_conf *info);
	int (*card_stop)(struct hw *hw);
	enum CHIPTYP (*get_chip_type)(struct hw *hw);
	int (*is_adc_source_selected)(struct hw *hw, enum ADCSRC source);
	int (*select_adc_source)(struct hw *hw, enum ADCSRC source);
	int (*line_out_unmute)(struct hw *hw, unsigned int state);
	int (*have_digit_io_switch)(struct hw *hw);

	/* SRC operations */
	int (*src_rsc_get_ctrl_blk)(void **rblk);
	int (*src_rsc_put_ctrl_blk)(void *blk);
	int (*src_set_state)(void *blk, unsigned int state);
	int (*src_set_bm)(void *blk, unsigned int bm);
	int (*src_set_rsr)(void *blk, unsigned int rsr);
	int (*src_set_sf)(void *blk, unsigned int sf);
	int (*src_set_wr)(void *blk, unsigned int wr);
	int (*src_set_pm)(void *blk, unsigned int pm);
	int (*src_set_rom)(void *blk, unsigned int rom);
	int (*src_set_vo)(void *blk, unsigned int vo);
	int (*src_set_st)(void *blk, unsigned int st);
	int (*src_set_ie)(void *blk, unsigned int ie);
	int (*src_set_ilsz)(void *blk, unsigned int ilsz);
	int (*src_set_bp)(void *blk, unsigned int bp);
	int (*src_set_cisz)(void *blk, unsigned int cisz);
	int (*src_set_ca)(void *blk, unsigned int ca);
	int (*src_set_sa)(void *blk, unsigned int sa);
	int (*src_set_la)(void *blk, unsigned int la);
	int (*src_set_pitch)(void *blk, unsigned int pitch);
	int (*src_set_clear_zbufs)(void *blk, unsigned int clear);
	int (*src_set_dirty)(void *blk, unsigned int flags);
	int (*src_set_dirty_all)(void *blk);
	int (*src_commit_write)(struct hw *hw, unsigned int idx, void *blk);
	int (*src_get_ca)(struct hw *hw, unsigned int idx, void *blk);
	unsigned int (*src_get_dirty)(void *blk);
	unsigned int (*src_dirty_conj_mask)(void);
	int (*src_mgr_get_ctrl_blk)(void **rblk);
	int (*src_mgr_put_ctrl_blk)(void *blk);
	int (*src_mgr_enbs_src)(void *blk, unsigned int idx); /* syncly enable src @idx */
	int (*src_mgr_enb_src)(void *blk, unsigned int idx); /* enable src @idx */
	int (*src_mgr_dsb_src)(void *blk, unsigned int idx); /* disable src @idx */
	int (*src_mgr_commit_write)(struct hw *hw, void *blk);

	/* SRC Input Mapper operations */
	int (*srcimp_mgr_get_ctrl_blk)(void **rblk);
	int (*srcimp_mgr_put_ctrl_blk)(void *blk);
	int (*srcimp_mgr_set_imaparc)(void *blk, unsigned int slot);
	int (*srcimp_mgr_set_imapuser)(void *blk, unsigned int user);
	int (*srcimp_mgr_set_imapnxt)(void *blk, unsigned int next);
	int (*srcimp_mgr_set_imapaddr)(void *blk, unsigned int addr);
	int (*srcimp_mgr_commit_write)(struct hw *hw, void *blk);

	/* AMIXER operations */
	int (*amixer_rsc_get_ctrl_blk)(void **rblk);
	int (*amixer_rsc_put_ctrl_blk)(void *blk);
	int (*amixer_mgr_get_ctrl_blk)(void **rblk);
	int (*amixer_mgr_put_ctrl_blk)(void *blk);
	int (*amixer_set_mode)(void *blk, unsigned int mode);
	int (*amixer_set_iv)(void *blk, unsigned int iv);
	int (*amixer_set_x)(void *blk, unsigned int x);
	int (*amixer_set_y)(void *blk, unsigned int y);
	int (*amixer_set_sadr)(void *blk, unsigned int sadr);
	int (*amixer_set_se)(void *blk, unsigned int se);
	int (*amixer_set_dirty)(void *blk, unsigned int flags);
	int (*amixer_set_dirty_all)(void *blk);
	int (*amixer_commit_write)(struct hw *hw, unsigned int idx, void *blk);
	int (*amixer_get_y)(void *blk);
	unsigned int (*amixer_get_dirty)(void *blk);

	/* DAIO operations */
	int (*dai_get_ctrl_blk)(void **rblk);
	int (*dai_put_ctrl_blk)(void *blk);
	int (*dai_srt_set_srco)(void *blk, unsigned int src);
	int (*dai_srt_set_srcm)(void *blk, unsigned int src);
	int (*dai_srt_set_rsr)(void *blk, unsigned int rsr);
	int (*dai_srt_set_drat)(void *blk, unsigned int drat);
	int (*dai_srt_set_ec)(void *blk, unsigned int ec);
	int (*dai_srt_set_et)(void *blk, unsigned int et);
	int (*dai_commit_write)(struct hw *hw, unsigned int idx, void *blk);
	int (*daio_mgr_get_ctrl_blk)(void **rblk);
	int (*daio_mgr_put_ctrl_blk)(void *blk);
	int (*daio_mgr_enb_dai)(void *blk, unsigned int idx);
	int (*daio_mgr_dsb_dai)(void *blk, unsigned int idx);
	int (*daio_mgr_enb_dao)(void *blk, unsigned int idx);
	int (*daio_mgr_dsb_dao)(void *blk, unsigned int idx);
	int (*daio_mgr_set_imaparc)(void *blk, unsigned int slot);
	int (*daio_mgr_set_imapnxt)(void *blk, unsigned int next);
	int (*daio_mgr_set_imapaddr)(void *blk, unsigned int addr);
	int (*daio_mgr_commit_write)(struct hw *hw, void *blk);

	struct pci_dev *pci;	/* the pci kernel structure of this card */
	int irq;
	u32 io_base;
	unsigned long mem_base;
} hw_t;

int create_hw_obj(struct pci_dev *pci, hw_t **rhw);
int destroy_hw_obj(hw_t *hw);

#endif /* CTHARDWARE_H */
