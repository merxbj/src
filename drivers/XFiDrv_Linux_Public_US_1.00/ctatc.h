/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	ctatc.h
 * 
 * @Brief   
 * This file contains the definition of the device resource management object. 
 *
 * @Author	Liu Chun
 * @Date 	Mar 28 2008
 * 
 */

#ifndef CTATC_H
#define CTATC_H

#include <linux/types.h>
#include <linux/spinlock_types.h>
#include <linux/pci.h>
#include <linux/timer.h>
#include <sound/driver.h>
#include <sound/core.h>

#include "ctvmem.h"
#include "ctresource.h"

enum CTALSADEVS {		/* Types of alsa devices */
	WAVE,
	MIXER,
	NUM_CTALSADEVS		/* This should always be the last */
};

enum CTCARDS {
	CTSB0760,
	CTHENDRIX,
	CTSB0880,
	NUM_CTCARDS		/* This should always be the last */
};

struct ct_atc_chip_sub_details {
	u16 subsys;
	const char *nm_model;
};

struct ct_atc_chip_details {
	u16 vendor;
	u16 device;
	const struct ct_atc_chip_sub_details *sub_details;
	const char *nm_card;
};

struct ct_atc;

/* alsa pcm stream descriptor */
struct ct_atc_pcm {
	struct snd_pcm_substream *substream;
	void (*interrupt)(struct ct_atc_pcm *apcm);
	volatile unsigned int started		: 1;
	volatile unsigned int stop_timer	: 1;
	struct timer_list timer;
	spinlock_t timer_lock;
	unsigned int position;

	/* Only mono and interleaved modes are supported now. */
	struct ct_vm_block *vm_block;
	void *src;			/* SRC for interacting with host memory */
	void **srccs;			/* SRCs for sample rate conversion */
	void **srcimps;			/* SRC Input Mappers */
	void **amixers;			/* AMIXERs for routing converted data */
	void *mono;			/* A SUM resource for mixing chs to one */
	unsigned int n_srcc	: 8;	/* Number of converting SRCs */
	unsigned int n_srcimp	: 8;	/* Number of SRC Input Mappers */
	unsigned int n_amixer	: 8;	/* Number of AMIXERs */
};

/* Chip resource management object */
struct ct_atc {
	struct pci_dev *pci;
	struct snd_card *card;
	int rsr; /* reference sample rate in Hz */
	int msr; /* master sample rate in rsr */

	const struct ct_atc_chip_details *chip_details;
	enum CTCARDS model;

	int (*create_alsa_devs)(struct ct_atc *atc); /* Create all alsa devices */

	struct ct_vm *vm;		/* device virtual memory manager for this card */
	int (*map_audio_buffer)(struct ct_atc *atc, struct ct_atc_pcm *apcm);
	void (*unmap_audio_buffer)(struct ct_atc *atc, struct ct_atc_pcm *apcm);
	unsigned long (*get_ptp_phys)(struct ct_atc *atc, int index);

	spinlock_t atc_lock;
	spinlock_t vm_lock;

	int (*pcm_playback_prepare)(struct ct_atc *atc, struct ct_atc_pcm *apcm);
	int (*pcm_playback_start)(struct ct_atc *atc, struct ct_atc_pcm *apcm);
	int (*pcm_playback_stop)(struct ct_atc *atc, struct ct_atc_pcm *apcm);
	int (*pcm_playback_position)(struct ct_atc *atc, 
				     struct ct_atc_pcm *apcm);
	int (*pcm_capture_prepare)(struct ct_atc *atc, struct ct_atc_pcm *apcm);
	int (*pcm_capture_start)(struct ct_atc *atc, struct ct_atc_pcm *apcm);
	int (*pcm_capture_stop)(struct ct_atc *atc, struct ct_atc_pcm *apcm);
	int (*pcm_capture_position)(struct ct_atc *atc, 
				     struct ct_atc_pcm *apcm);
	int (*pcm_release_resources)(struct ct_atc *atc, struct ct_atc_pcm *apcm);
	int (*select_line_in)(struct ct_atc *atc);
	int (*select_mic_in)(struct ct_atc *atc);
	int (*select_digit_io)(struct ct_atc *atc);
	int (*line_out_unmute)(struct ct_atc *atc, unsigned char state);
	int (*line_in_unmute)(struct ct_atc *atc, unsigned char state);
	int (*spdif_out_unmute)(struct ct_atc *atc, unsigned char state);
	int (*spdif_in_unmute)(struct ct_atc *atc, unsigned char state);
	int (*have_digit_io_switch)(struct ct_atc *atc);

	/* Don't touch! Used for internal object. */
	void *rsc_mgrs[NUM_RSCTYP];	/* chip resource managers */
	void *mixer;			/* internal mixer object */
	void *hw;			/* chip specific hardware access object */
	void *lo_f;			/* line-out-front resource */
	void *li_m;			/* line/mic-in resource */
	void *spdifo;			/* spdif-out resource */
	void *spdifi;			/* spdif-in resource */
	void **pcm;			/* SUMs for collecting all pcm stream */
	void **srcs;			/* Sample Rate Converters for input signal */
	void **srcimps;			/* input mappers for SRCs */
	unsigned int n_src	: 8;
	unsigned int n_srcimp	: 8;
	unsigned int n_pcm	: 8;
};

int __devinit ct_atc_create(struct snd_card *card, 
			    struct pci_dev *pci, 
			    struct ct_atc **ratc); 

#endif /* CTATC_H */
