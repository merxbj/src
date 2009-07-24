/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	ctmixer.h
 * 
 * @Brief   
 * This file contains the definition of the mixer device functions. 
 *
 * @Author	Liu Chun
 * @Date 	Mar 28 2008
 * 
 */

#ifndef CTMIXER_H
#define CTMIXER_H

#include "ctatc.h"
#include "ctresource.h"

#define INIT_VOL	0x1c00

/* alsa mixer descriptor */
struct ct_mixer {
	struct ct_atc *atc;

	void **amixers;		/* amixer resources for volume control */
	void **sums;		/* sum resources for signal collection */
	unsigned int sharer_changed; /* A bit-map to indicate state of shared amixer */
	unsigned int switch_state; /* A bit-map to indicate state of switches */

	int (*get_wave_out_port)(struct ct_mixer *mixer, 
				 struct rsc **rleft, struct rsc **rright);
	int (*get_spdif_out_port)(struct ct_mixer *mixer, 
				 struct rsc **rleft, struct rsc **rright);
	int (*get_pcm_out_port)(struct ct_mixer *mixer,  
				struct rsc **rleft, struct rsc **rright);
	int (*set_line_in_left)(struct ct_mixer *mixer, struct rsc *rsc);
	int (*set_line_in_right)(struct ct_mixer *mixer, struct rsc *rsc);
	int (*set_mic_in_left)(struct ct_mixer *mixer, struct rsc *rsc);
	int (*set_mic_in_right)(struct ct_mixer *mixer, struct rsc *rsc);
	int (*set_spdif_in_left)(struct ct_mixer *mixer, struct rsc *rsc);
	int (*set_spdif_in_right)(struct ct_mixer *mixer, struct rsc *rsc);
	int (*set_pcm_in_left)(struct ct_mixer *mixer, struct rsc *rsc);
	int (*set_pcm_in_right)(struct ct_mixer *mixer, struct rsc *rsc);
};

int ct_alsa_mix_create(struct ct_atc *atc, 
		       enum CTALSADEVS device,
		       const char *device_name);
int ct_mixer_create(struct ct_atc *atc, struct ct_mixer **rmixer);
int ct_mixer_destroy(struct ct_mixer *mixer);

#endif /* CTMIXER_H */
