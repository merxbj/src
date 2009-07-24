/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	ctmixer.c
 * 
 * @Brief   
 * This file contains the implementation of alsa mixer device functions. 
 *
 * @Author	Liu Chun
 * @Date 	May 28 2008
 * 
 */


#include "ctmixer.h"
#include "ctamixer.h"
#include "ctutils.h"
#include <sound/core.h>
#include <sound/control.h>
#include <linux/slab.h>

enum CT_SUM_CTL {
	SUM_IN,
	SUM_IN_C,

	NUM_CT_SUMS
};

enum CT_AMIXER_CTL {
	/* volume control mixers */
	AMIXER_MASTER,
	AMIXER_PCM,
	AMIXER_SPDIFI,
	AMIXER_LINEIN,
	AMIXER_MIC,
	AMIXER_SPDIFO,
	AMIXER_WAVE,
	AMIXER_MASTER_C,
	AMIXER_PCM_C,
	AMIXER_SPDIFI_C,
	AMIXER_LINEIN_C,
	AMIXER_MIC_C,

	/* this should always be the last one */
	NUM_CT_AMIXERS
};

enum CTALSA_MIXER_CTL {
	/* volume control mixers */
	MIXER_MASTER_P,
	MIXER_PCM_P,
	MIXER_LINEIN_P,
	MIXER_MIC_P,
	MIXER_SPDIFI_P,
	MIXER_SPDIFO_P,
	MIXER_WAVE_P,
	MIXER_MASTER_C,
	MIXER_PCM_C,
	MIXER_LINEIN_C,
	MIXER_MIC_C,
	MIXER_SPDIFI_C,

	/* switch control mixers */
	MIXER_PCM_C_S,
	MIXER_LINEIN_C_S,
	MIXER_MIC_C_S,
	MIXER_SPDIFI_C_S,
	MIXER_SPDIFO_P_S,
	MIXER_WAVE_P_S,
	MIXER_DIGITAL_IO_S,

	/* this should always be the last one */
	NUM_CTALSA_MIXERS
};

#define VOL_MIXER_START		MIXER_MASTER_P
#define VOL_MIXER_END		MIXER_SPDIFI_C
#define VOL_MIXER_NUM		(VOL_MIXER_END - VOL_MIXER_START + 1)
#define SWH_MIXER_START		MIXER_PCM_C_S
#define SWH_MIXER_END		MIXER_DIGITAL_IO_S
#define SWH_CAPTURE_START	MIXER_PCM_C_S
#define SWH_CAPTURE_END		MIXER_SPDIFI_C_S

#define CHN_NUM		2

struct ct_kcontrol_init {
	unsigned char ctl;
	char *name; 
};

static struct ct_kcontrol_init 
ct_kcontrol_init_table[NUM_CTALSA_MIXERS] = {
	[MIXER_MASTER_P] = {
		.ctl = 1,
		.name = "Master Playback Volume",
	},
	[MIXER_MASTER_C] = {
		.ctl = 1,
		.name = "Master Capture Volume",
	},
	[MIXER_PCM_P] = {
		.ctl = 1,
		.name = "PCM Playback Volume",
	},
	[MIXER_PCM_C] = {
		.ctl = 1,
		.name = "PCM Capture Volume",
	},
	[MIXER_LINEIN_P] = {
		.ctl = 1,
		.name = "Line-in Playback Volume",
	},
	[MIXER_LINEIN_C] = {
		.ctl = 1,
		.name = "Line-in Capture Volume",
	},
	[MIXER_MIC_P] = {
		.ctl = 1,
		.name = "Mic Playback Volume",
	},
	[MIXER_MIC_C] = {
		.ctl = 1,
		.name = "Mic Capture Volume",
	},
	[MIXER_SPDIFI_P] = {
		.ctl = 1,
		.name = "S/PDIF-in Playback Volume",
	},
	[MIXER_SPDIFI_C] = {
		.ctl = 1,
		.name = "S/PDIF-in Capture Volume",
	},
	[MIXER_SPDIFO_P] = {
		.ctl = 1,
		.name = "S/PDIF-out Playback Volume",
	},
	[MIXER_WAVE_P] = {
		.ctl = 1,
		.name = "Wave Playback Volume",
	},

	[MIXER_PCM_C_S] = {
		.ctl = 1,
		.name = "PCM Capture Switch",
	},
	[MIXER_LINEIN_C_S] = {
		.ctl = 1,
		.name = "Line-in Capture Switch",
	},
	[MIXER_MIC_C_S] = {
		.ctl = 1,
		.name = "Mic Capture Switch",
	},
	[MIXER_SPDIFI_C_S] = {
		.ctl = 1,
		.name = "S/PDIF-in Capture Switch",
	},
	[MIXER_SPDIFO_P_S] = {
		.ctl = 1,
		.name = "S/PDIF-out Playback Switch",
	},
	[MIXER_WAVE_P_S] = {
		.ctl = 1,
		.name = "Wave Playback Switch",
	},
	[MIXER_DIGITAL_IO_S] = {
		.ctl = 0,
		.name = "Digit-IO Playback Switch",
	}
};

static void 
ct_mixer_recording_select(struct ct_mixer *mixer, enum CT_AMIXER_CTL type);

static void 
ct_mixer_recording_unselect(struct ct_mixer *mixer, enum CT_AMIXER_CTL type);

static struct snd_kcontrol *kctls[2] = {NULL};

static enum CT_AMIXER_CTL get_amixer_index(enum CTALSA_MIXER_CTL alsa_index)
{
	switch (alsa_index) {
	case MIXER_MASTER_P:
	case MIXER_MASTER_C:	return AMIXER_MASTER;
	case MIXER_PCM_P:
	case MIXER_PCM_C:
	case MIXER_PCM_C_S:	return AMIXER_PCM;
	case MIXER_LINEIN_P:
	case MIXER_LINEIN_C:
	case MIXER_LINEIN_C_S:	return AMIXER_LINEIN;
	case MIXER_MIC_P:
	case MIXER_MIC_C:
	case MIXER_MIC_C_S:	return AMIXER_MIC;
	case MIXER_SPDIFI_P:
	case MIXER_SPDIFI_C:
	case MIXER_SPDIFI_C_S:	return AMIXER_SPDIFI;
	case MIXER_SPDIFO_P:	return AMIXER_SPDIFO;
	case MIXER_WAVE_P:	return AMIXER_WAVE;
	default:		return NUM_CT_AMIXERS;
	}
}

static enum CTALSA_MIXER_CTL get_amixer_sharer(enum CTALSA_MIXER_CTL alsa_index)
{
	switch (alsa_index) {
	case MIXER_MASTER_P:	return MIXER_MASTER_C;
	case MIXER_MASTER_C:	return MIXER_MASTER_P;
	case MIXER_PCM_P:	return MIXER_PCM_C;
	case MIXER_PCM_C:	return MIXER_PCM_P;
	case MIXER_LINEIN_P:	return MIXER_LINEIN_C;
	case MIXER_LINEIN_C:	return MIXER_LINEIN_P;
	case MIXER_MIC_P:	return MIXER_MIC_C;
	case MIXER_MIC_C:	return MIXER_MIC_P;
	case MIXER_SPDIFI_P:	return MIXER_SPDIFI_C;
	case MIXER_SPDIFI_C:	return MIXER_SPDIFI_P;
	case MIXER_LINEIN_C_S:	return MIXER_MIC_C_S;
	case MIXER_MIC_C_S:	return MIXER_LINEIN_C_S;
	default:		return NUM_CTALSA_MIXERS;
	}
}

static enum CT_AMIXER_CTL get_recording_amixer(enum CT_AMIXER_CTL index)
{
	CTASSERT(index < AMIXER_SPDIFO);
	return (index + 7);
}

static unsigned char
get_switch_state(struct ct_mixer *mixer, enum CTALSA_MIXER_CTL type)
{
	CTASSERT(type >= SWH_MIXER_START && type <= SWH_MIXER_END);
	return (mixer->switch_state & (0x1 << (type - SWH_MIXER_START))) ? 1 : 0;
}

static void
set_switch_state(struct ct_mixer *mixer, enum CTALSA_MIXER_CTL type, unsigned char state)
{
	CTASSERT(type >= SWH_MIXER_START && type <= SWH_MIXER_END);
	if (state) {
		mixer->switch_state |= (0x1 << (type - SWH_MIXER_START));
	} else {
		mixer->switch_state &= ~(0x1 << (type - SWH_MIXER_START));
	}
}

static unsigned char 
is_sharer_changed(unsigned int sharer_changed, enum CTALSA_MIXER_CTL type)
{
	if (NUM_CTALSA_MIXERS <= get_amixer_sharer(type)) {
		return 0;
	}
	return (sharer_changed & (0x1 << type)) ? 1 : 0;
}

static void 
set_sharer_changed(unsigned int *sharer_changed, enum CTALSA_MIXER_CTL type)
{
	if (NUM_CTALSA_MIXERS > get_amixer_sharer(type)){
		(*sharer_changed) |= (0x1 << get_amixer_sharer(type));
	}
}

static void 
clear_sharer_changed(unsigned int *sharer_changed, enum CTALSA_MIXER_CTL type)
{
	if (NUM_CTALSA_MIXERS > get_amixer_sharer(type)){
		(*sharer_changed) &= ~(0x1 << type);
	}
}

/* Map integer value ranging from 0 to 65535 to 14-bit float value ranging
 * from 2^-6 to (1+1023/1024) */
static unsigned int uint16_to_float14(unsigned int x)
{
	unsigned int i = 0;

	if (x < 17) {
		return 0;
	}

	x *= 2031;
	x /= 65535;
	x += 16;

	/* i <= 6 */
	for (i = 0; !(x & 0x400); i++) {
		x <<= 1;
	}
	
	x = (((7 - i) & 0x7) << 10) | (x & 0x3ff);

	return x;
}

static unsigned int float14_to_uint16(unsigned int x)
{
	unsigned int e = 0;

	if (!x) {
		return x;
	}

	e = (x >> 10) & 0x7;
	x &= 0x3ff;
	x += 1024;
	x >>= (7 - e);
	x -= 16;
	x *= 65535;
	x /= 2031;

	return x;
}

static int ct_alsa_mix_volume_info(struct snd_kcontrol *kcontrol, 
				   struct snd_ctl_elem_info *uinfo)
{
	uinfo->type = SNDRV_CTL_ELEM_TYPE_INTEGER;
	uinfo->count = 2;
	uinfo->value.integer.min = 0;
	uinfo->value.integer.max = 43690;
	uinfo->value.integer.step = 128;

	return 0;
}

static int ct_alsa_mix_volume_get(struct snd_kcontrol *kcontrol,
				  struct snd_ctl_elem_value *ucontrol)
{
	struct ct_atc *atc = snd_kcontrol_chip(kcontrol);
	enum CT_AMIXER_CTL type = get_amixer_index(kcontrol->private_value);
	struct amixer *amixer = NULL;
	int i = 0;

	/*CTDPF("%s(%d) is called, type = %d\n", __func__, __LINE__, type);*/

	for (i = 0; i < 2; i++) {
		amixer = ((struct ct_mixer *)atc->mixer)->amixers[type*CHN_NUM+i];
		/* Convert 14-bit float-point scale to 16-bit integer volume */
		ucontrol->value.integer.value[i] = 
			(float14_to_uint16(amixer->ops->get_scale(amixer)) & 0xffff);
	}

	return 0;
}

static int ct_alsa_mix_volume_put(struct snd_kcontrol *kcontrol,
				  struct snd_ctl_elem_value *ucontrol)
{
	struct ct_atc *atc = snd_kcontrol_chip(kcontrol);
	struct ct_mixer *mixer = atc->mixer;
	enum CT_AMIXER_CTL type = get_amixer_index(kcontrol->private_value);
	struct amixer *amixer = NULL;
	int i = 0, change = 0, val = 0;

	/*CTDPF("%s(%d) is called, type = %d\n", __func__, __LINE__, kcontrol->private_value);*/

	/* If the sharer of this kcontrol has changed the volume, return directly. */
	if (is_sharer_changed(mixer->sharer_changed, kcontrol->private_value)) {
		clear_sharer_changed(&mixer->sharer_changed, kcontrol->private_value);
		return 0;
	}

	for (i = 0; i < 2; i++) {
		/* Convert 16-bit integer volume to 14-bit float-point scale */
		val = (ucontrol->value.integer.value[i] & 0xffff);
		amixer = mixer->amixers[type*CHN_NUM+i];
		if ((float14_to_uint16(amixer->ops->get_scale(amixer)) & 0xff80) 
				!= (val & 0xff80)) {
			val = uint16_to_float14(val);
			amixer->ops->set_scale(amixer, val);
			amixer->ops->commit_write(amixer);
			change = 1;
			/* Inform my sharer that the volume has changed. */
			set_sharer_changed(&mixer->sharer_changed, 
						kcontrol->private_value);
			/* Synchronize Master recording AMIXER to Master AMIXER. */
			if (AMIXER_MASTER == type) {
				amixer = mixer->amixers[AMIXER_MASTER_C*CHN_NUM+i];
				amixer->ops->set_scale(amixer, val);
				amixer->ops->commit_write(amixer);
			}
		}
	}

	return change;
}

static struct snd_kcontrol_new vol_ctl = {
	.access		= SNDRV_CTL_ELEM_ACCESS_READWRITE,
	.iface		= SNDRV_CTL_ELEM_IFACE_MIXER,
	.info		= ct_alsa_mix_volume_info,
	.get		= ct_alsa_mix_volume_get,
	.put		= ct_alsa_mix_volume_put
};

static void 
do_line_mic_switch(struct ct_atc *atc, enum CTALSA_MIXER_CTL type)
{

	if (MIXER_LINEIN_C_S == type) {
		atc->select_line_in(atc);
		set_switch_state(atc->mixer, MIXER_MIC_C_S, 0);
		snd_ctl_notify(atc->card, SNDRV_CTL_EVENT_MASK_VALUE, &kctls[1]->id);
	} else if (MIXER_MIC_C_S == type) {
		atc->select_mic_in(atc);
		set_switch_state(atc->mixer, MIXER_LINEIN_C_S, 0);
		snd_ctl_notify(atc->card, SNDRV_CTL_EVENT_MASK_VALUE, &kctls[0]->id);
	}
}

static void 
do_digit_io_switch(struct ct_atc *atc, int state)
{
	struct ct_mixer *mixer = atc->mixer;

	if (state) {
		atc->select_digit_io(atc);
		atc->spdif_out_unmute(atc, 
				get_switch_state(mixer, MIXER_SPDIFO_P_S));
		atc->spdif_in_unmute(atc, 1);
		atc->line_in_unmute(atc, 0);
		return;
	}

	if (get_switch_state(mixer, MIXER_LINEIN_C_S)) {
		atc->select_line_in(atc);
	} else if (get_switch_state(mixer, MIXER_MIC_C_S)) {
		atc->select_mic_in(atc);
	}
	atc->spdif_out_unmute(atc, 0);
	atc->spdif_in_unmute(atc, 0);
	atc->line_in_unmute(atc, 1);
	return;
}

static int ct_alsa_mix_switch_info(struct snd_kcontrol *kcontrol, 
				   struct snd_ctl_elem_info *uinfo)
{
	uinfo->type = SNDRV_CTL_ELEM_TYPE_BOOLEAN;
	uinfo->count = 1;
	uinfo->value.integer.min = 0;
	uinfo->value.integer.max = 1;
	uinfo->value.integer.step = 1;

	return 0;
}

static int ct_alsa_mix_switch_get(struct snd_kcontrol *kcontrol,
				  struct snd_ctl_elem_value *ucontrol)
{
	struct ct_mixer *mixer = ((struct ct_atc *)snd_kcontrol_chip(kcontrol))->mixer;
	enum CTALSA_MIXER_CTL type = kcontrol->private_value;

	ucontrol->value.integer.value[0] = get_switch_state(mixer, type);
	CTDPF("%s(%d) is called, type = %d, value = %d\n", __func__, __LINE__, type, ucontrol->value.integer.value[0]);
	return 0;
}

static int ct_alsa_mix_switch_put(struct snd_kcontrol *kcontrol,
				  struct snd_ctl_elem_value *ucontrol)
{
	struct ct_atc *atc = snd_kcontrol_chip(kcontrol);
	struct ct_mixer *mixer = atc->mixer;
	enum CTALSA_MIXER_CTL type = kcontrol->private_value;
	int state = 0;

	state = ucontrol->value.integer.value[0];
	CTDPF("%s(%d) is called, type = %d, value = %d\n", __func__, __LINE__, type, state);
	if (get_switch_state(mixer, type) == state) {
		return 0;
	}

	set_switch_state(mixer, type, state);
	/* Do changes in mixer. */
	if((SWH_CAPTURE_START <= type) && (SWH_CAPTURE_END >= type)) {
		if (state) {
			CTDPF("%s(%d) select type = %d\n", __func__, __LINE__, get_amixer_index(type));
			ct_mixer_recording_select(mixer, get_amixer_index(type));
		} else {
			CTDPF("%s(%d) unselect type = %d\n", __func__, __LINE__, get_amixer_index(type));
			ct_mixer_recording_unselect(mixer, get_amixer_index(type));
		}
	}
	/* Do changes out of mixer. */
	if (state && (MIXER_LINEIN_C_S == type || MIXER_MIC_C_S == type)) {
		do_line_mic_switch(atc, type);
	} else if (MIXER_WAVE_P_S == type) {
		atc->line_out_unmute(atc, state);
	} else if (MIXER_SPDIFO_P_S == type) {
		atc->spdif_out_unmute(atc, state);
	} else if (MIXER_DIGITAL_IO_S == type) {
		do_digit_io_switch(atc, state); 
	}

	return 1;
}

static struct snd_kcontrol_new swh_ctl = {
	.access		= SNDRV_CTL_ELEM_ACCESS_READWRITE,
	.iface		= SNDRV_CTL_ELEM_IFACE_MIXER,
	.info		= ct_alsa_mix_switch_info,
	.get		= ct_alsa_mix_switch_get,
	.put		= ct_alsa_mix_switch_put
};

static int ct_mixer_kcontrol_new(struct ct_mixer *mixer, struct snd_kcontrol_new *new)
{
	struct snd_kcontrol *kctl = NULL;
	int err = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	kctl = snd_ctl_new1(new, mixer->atc);
	if (NULL == kctl) {
		return -ENOMEM;
	}

	if ((err = snd_ctl_add(mixer->atc->card, kctl))) {
		return err;
	}

	if (MIXER_LINEIN_C_S == new->private_value) {
		kctls[0] = kctl;
	} else if (MIXER_MIC_C_S == new->private_value) {
		kctls[1] = kctl;
	}

	return 0;
}

static int ct_mixer_kcontrols_create(struct ct_mixer *mixer)
{
	enum CTALSA_MIXER_CTL type = 0;
	struct ct_atc *atc = mixer->atc;
	int err = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	/* Create snd kcontrol instances on demand */
	for (type = VOL_MIXER_START; type <= VOL_MIXER_END; type++) {
		if (ct_kcontrol_init_table[type].ctl) {
			vol_ctl.name = ct_kcontrol_init_table[type].name;
			vol_ctl.private_value = (unsigned long)type;
			if ((err = ct_mixer_kcontrol_new(mixer, &vol_ctl))) {
				return err;
			}
		}
	}

	ct_kcontrol_init_table[MIXER_DIGITAL_IO_S].ctl = 
					atc->have_digit_io_switch(atc);
	for (type = SWH_MIXER_START; type <= SWH_MIXER_END; type++) {
		if (ct_kcontrol_init_table[type].ctl) {
			swh_ctl.name = ct_kcontrol_init_table[type].name;
			swh_ctl.private_value = (unsigned long)type;
			if ((err = ct_mixer_kcontrol_new(mixer, &swh_ctl))) {
				return err;
			}
		}
	}

	return 0;
}

static void 
ct_mixer_recording_select(struct ct_mixer *mixer, enum CT_AMIXER_CTL type)
{
	struct amixer *amix_d = NULL, *amix_s = NULL;
	int i = 0;

	for (i = 0; i < 2; i++) {
		amix_s = mixer->amixers[type*CHN_NUM+i];
		amix_d = mixer->amixers[get_recording_amixer(type)*CHN_NUM+i];
		amix_d->ops->set_input(amix_d, &amix_s->rsc);
		amix_d->ops->commit_write(amix_d);
	}
}

static void 
ct_mixer_recording_unselect(struct ct_mixer *mixer, enum CT_AMIXER_CTL type)
{
	struct amixer *amix_d = NULL;
	int i = 0;

	for (i = 0; i < 2; i++) {
		amix_d = mixer->amixers[get_recording_amixer(type)*CHN_NUM+i];
		amix_d->ops->set_input(amix_d, NULL);
		amix_d->ops->commit_write(amix_d);
	}
}

static int ct_mixer_get_resources(struct ct_mixer *mixer)
{
	struct sum_mgr *sum_mgr = NULL;
	struct sum *sum = NULL;
	struct sum_desc sum_desc = {0};
	struct amixer_mgr *amixer_mgr = NULL;
	struct amixer *amixer = NULL;
	struct amixer_desc am_desc = {0};
	int err = 0;
	int i = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	/* Allocate sum resources for mixer obj */
	sum_mgr = (struct sum_mgr *)mixer->atc->rsc_mgrs[SUM];
	sum_desc.msr = mixer->atc->msr;
	for (i = 0; i < (NUM_CT_SUMS * CHN_NUM); i++) {
		if ((err = sum_mgr->get_sum(sum_mgr, &sum_desc, &sum))) {
			CTDPF("%s(%d) Failed to get sum resources for front output!\n", __func__, __LINE__);
			break;
		}
		mixer->sums[i] = sum;
	}
	if (err) {
		goto error1;
	}

	/* Allocate amixer resources for mixer obj */
	amixer_mgr = (struct amixer_mgr *)mixer->atc->rsc_mgrs[AMIXER];
	am_desc.msr = mixer->atc->msr;
	for (i = 0; i < (NUM_CT_AMIXERS * CHN_NUM); i++) {
		if ((err = amixer_mgr->get_amixer(amixer_mgr, &am_desc, &amixer))) {
			CTDPF("%s(%d) Failed to get amixer resources for mixer obj!\n", __func__, __LINE__);
			break;
		}
		mixer->amixers[i] = amixer;
	}
	if (err) {
		goto error2;
	}
	
	return 0;

error2:	
	for (i = 0; i < (NUM_CT_AMIXERS * CHN_NUM); i++) {
		if (NULL != mixer->amixers[i]) {
			amixer = mixer->amixers[i];
			amixer_mgr->put_amixer(amixer_mgr, amixer);
			mixer->amixers[i] = NULL;
		}
	}
error1:	
	for (i = 0; i < (NUM_CT_SUMS * CHN_NUM); i++) {
		if (NULL != mixer->sums[i]) {
			sum_mgr->put_sum(sum_mgr, (struct sum *)mixer->sums[i]);
			mixer->sums[i] = NULL;
		}
	}

	return err;
}

static int ct_mixer_get_mem(struct ct_mixer **rmixer)
{
	struct ct_mixer *mixer = NULL;
	int err = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	*rmixer = NULL;
	/* Allocate mem for mixer obj */
	mixer = kzalloc(sizeof(*mixer), GFP_KERNEL);
	if (NULL == mixer) {
		return -ENOMEM;
	}
	mixer->amixers = kzalloc(sizeof(void*)*(NUM_CT_AMIXERS*CHN_NUM), GFP_KERNEL);
	if (NULL == mixer->amixers) {
		err = -ENOMEM;
		goto error1;
	}
	mixer->sums = kzalloc(sizeof(void*)*(NUM_CT_SUMS*CHN_NUM), GFP_KERNEL);
	if (NULL == mixer->sums) {
		err = -ENOMEM;
		goto error2;
	}

	*rmixer = mixer;
	return 0;

error2:	
	kfree(mixer->amixers);
error1:	
	kfree(mixer);
	return err;
}

static int ct_mixer_topology_build(struct ct_mixer *mixer)
{
	struct sum *sum = NULL;
	struct amixer *amix_d = NULL, *amix_s = NULL;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	/* Build topology from destination to source */

	/* Set up Master mixer */
	CTDPF("%s(%d) Set up Master mixer\n", __func__, __LINE__);
	amix_d = mixer->amixers[AMIXER_MASTER*CHN_NUM];
	sum = mixer->sums[SUM_IN*CHN_NUM];
	amix_d->ops->setup(amix_d, &sum->rsc, INIT_VOL, NULL);
	amix_d = mixer->amixers[AMIXER_MASTER*CHN_NUM+1];
	sum = mixer->sums[SUM_IN*CHN_NUM+1];
	amix_d->ops->setup(amix_d, &sum->rsc, INIT_VOL, NULL);

	/* Set up Wave-out mixer */
	CTDPF("%s(%d) Set up Wave-out mixer\n", __func__, __LINE__);
	amix_d = mixer->amixers[AMIXER_WAVE*CHN_NUM];
	amix_s = mixer->amixers[AMIXER_MASTER*CHN_NUM];
	amix_d->ops->setup(amix_d, &amix_s->rsc, INIT_VOL, NULL);
	amix_d = mixer->amixers[AMIXER_WAVE*CHN_NUM+1];
	amix_s = mixer->amixers[AMIXER_MASTER*CHN_NUM+1];
	amix_d->ops->setup(amix_d, &amix_s->rsc, INIT_VOL, NULL);
	set_switch_state(mixer, MIXER_WAVE_P_S, 1);

	/* Set up S/PDIF-out mixer */
	CTDPF("%s(%d) Set up S/PDIF-out mixer\n", __func__, __LINE__);
	amix_d = mixer->amixers[AMIXER_SPDIFO*CHN_NUM];
	amix_s = mixer->amixers[AMIXER_MASTER*CHN_NUM];
	amix_d->ops->setup(amix_d, &amix_s->rsc, INIT_VOL, NULL);
	amix_d = mixer->amixers[AMIXER_SPDIFO*CHN_NUM+1];
	amix_s = mixer->amixers[AMIXER_MASTER*CHN_NUM+1];
	amix_d->ops->setup(amix_d, &amix_s->rsc, INIT_VOL, NULL);
	set_switch_state(mixer, MIXER_SPDIFO_P_S, 1);

	/* Set up PCM-in mixer */
	CTDPF("%s(%d) Set up PCM-in mixer\n", __func__, __LINE__);
	amix_d = mixer->amixers[AMIXER_PCM*CHN_NUM];
	sum = mixer->sums[SUM_IN*CHN_NUM];
	amix_d->ops->setup(amix_d, NULL, INIT_VOL, sum);
	amix_d = mixer->amixers[AMIXER_PCM*CHN_NUM+1];
	sum = mixer->sums[SUM_IN*CHN_NUM+1];
	amix_d->ops->setup(amix_d, NULL, INIT_VOL, sum);

	/* Set up Line-in mixer */
	CTDPF("%s(%d) Set up Line-in mixer\n", __func__, __LINE__);
	amix_d = mixer->amixers[AMIXER_LINEIN*CHN_NUM];
	sum = mixer->sums[SUM_IN*CHN_NUM];
	amix_d->ops->setup(amix_d, NULL, INIT_VOL, sum);
	amix_d = mixer->amixers[AMIXER_LINEIN*CHN_NUM+1];
	sum = mixer->sums[SUM_IN*CHN_NUM+1];
	amix_d->ops->setup(amix_d, NULL, INIT_VOL, sum);

	/* Set up Mic-in mixer */
	CTDPF("%s(%d) Set up Mic-in mixer\n", __func__, __LINE__);
	amix_d = mixer->amixers[AMIXER_MIC*CHN_NUM];
	sum = mixer->sums[SUM_IN*CHN_NUM];
	amix_d->ops->setup(amix_d, NULL, INIT_VOL, sum);
	amix_d = mixer->amixers[AMIXER_MIC*CHN_NUM+1];
	sum = mixer->sums[SUM_IN*CHN_NUM+1];
	amix_d->ops->setup(amix_d, NULL, INIT_VOL, sum);

	/* Set up S/PDIF-in mixer */
	CTDPF("%s(%d) Set up S/PDIF-in mixer\n", __func__, __LINE__);
	amix_d = mixer->amixers[AMIXER_SPDIFI*CHN_NUM];
	sum = mixer->sums[SUM_IN*CHN_NUM];
	amix_d->ops->setup(amix_d, NULL, INIT_VOL, sum);
	amix_d = mixer->amixers[AMIXER_SPDIFI*CHN_NUM+1];
	sum = mixer->sums[SUM_IN*CHN_NUM+1];
	amix_d->ops->setup(amix_d, NULL, INIT_VOL, sum);

	/* Set up Master recording mixer */
	CTDPF("%s(%d) Set up Master recording mixer\n", __func__, __LINE__);
	amix_d = mixer->amixers[AMIXER_MASTER_C*CHN_NUM];
	sum = mixer->sums[SUM_IN_C*CHN_NUM];
	amix_d->ops->setup(amix_d, &sum->rsc, INIT_VOL, NULL);
	amix_d = mixer->amixers[AMIXER_MASTER_C*CHN_NUM+1];
	sum = mixer->sums[SUM_IN_C*CHN_NUM+1];
	amix_d->ops->setup(amix_d, &sum->rsc, INIT_VOL, NULL);

	/* Set up PCM-in recording mixer */
	CTDPF("%s(%d) Set up PCM-in recording mixer\n", __func__, __LINE__);
	amix_d = mixer->amixers[AMIXER_PCM_C*CHN_NUM];
	amix_s = mixer->amixers[AMIXER_PCM*CHN_NUM];
	sum = mixer->sums[SUM_IN_C*CHN_NUM];
	amix_d->ops->setup(amix_d, &amix_s->rsc, INIT_VOL, sum);
	amix_d = mixer->amixers[AMIXER_PCM_C*CHN_NUM+1];
	amix_s = mixer->amixers[AMIXER_PCM*CHN_NUM+1];
	sum = mixer->sums[SUM_IN_C*CHN_NUM+1];
	amix_d->ops->setup(amix_d, &amix_s->rsc, INIT_VOL, sum);
	set_switch_state(mixer, MIXER_PCM_C_S, 1);

	/* Set up Line-in recording mixer */
	CTDPF("%s(%d) Set up Line-in recording mixer\n", __func__, __LINE__);
	amix_d = mixer->amixers[AMIXER_LINEIN_C*CHN_NUM];
	amix_s = mixer->amixers[AMIXER_LINEIN*CHN_NUM];
	sum = mixer->sums[SUM_IN_C*CHN_NUM];
	amix_d->ops->setup(amix_d, &amix_s->rsc, INIT_VOL, sum);
	amix_d = mixer->amixers[AMIXER_LINEIN_C*CHN_NUM+1];
	amix_s = mixer->amixers[AMIXER_LINEIN*CHN_NUM+1];
	sum = mixer->sums[SUM_IN_C*CHN_NUM+1];
	amix_d->ops->setup(amix_d, &amix_s->rsc, INIT_VOL, sum);
	set_switch_state(mixer, MIXER_LINEIN_C_S, 1);

	/* Set up Mic-in recording mixer */
	CTDPF("%s(%d) Set up Mic-in recording mixer\n", __func__, __LINE__);
	amix_d = mixer->amixers[AMIXER_MIC_C*CHN_NUM];
	amix_s = mixer->amixers[AMIXER_MIC*CHN_NUM];
	sum = mixer->sums[SUM_IN_C*CHN_NUM];
	amix_d->ops->setup(amix_d, &amix_s->rsc, INIT_VOL, sum);
	amix_d = mixer->amixers[AMIXER_MIC_C*CHN_NUM+1];
	amix_s = mixer->amixers[AMIXER_MIC*CHN_NUM+1];
	sum = mixer->sums[SUM_IN_C*CHN_NUM+1];
	amix_d->ops->setup(amix_d, &amix_s->rsc, INIT_VOL, sum);
	set_switch_state(mixer, MIXER_MIC_C_S, 0);

	/* Set up S/PDIF-in recording mixer */
	CTDPF("%s(%d) Set up S/PDIF-in recording mixer\n", __func__, __LINE__);
	amix_d = mixer->amixers[AMIXER_SPDIFI_C*CHN_NUM];
	amix_s = mixer->amixers[AMIXER_SPDIFI*CHN_NUM];
	sum = mixer->sums[SUM_IN_C*CHN_NUM];
	amix_d->ops->setup(amix_d, &amix_s->rsc, INIT_VOL, sum);
	amix_d = mixer->amixers[AMIXER_SPDIFI_C*CHN_NUM+1];
	amix_s = mixer->amixers[AMIXER_SPDIFI*CHN_NUM+1];
	sum = mixer->sums[SUM_IN_C*CHN_NUM+1];
	amix_d->ops->setup(amix_d, &amix_s->rsc, INIT_VOL, sum);
	set_switch_state(mixer, MIXER_SPDIFI_C_S, 1);

	return 0;
}

static int mixer_set_input_port(struct amixer *amixer, struct rsc *rsc)
{
	amixer->ops->set_input(amixer, rsc);
	amixer->ops->commit_write(amixer);

	return 0;
}

static int mixer_set_line_in_left(struct ct_mixer *mixer, struct rsc *rsc)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	/* Set Line-in left mixer */
	mixer_set_input_port(mixer->amixers[AMIXER_LINEIN*CHN_NUM], rsc);

	return 0;
}

static int mixer_set_line_in_right(struct ct_mixer *mixer, struct rsc *rsc)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	/* Set Line-in right mixer */
	mixer_set_input_port(mixer->amixers[AMIXER_LINEIN*CHN_NUM+1], rsc);

	return 0;
}

static int mixer_set_mic_in_left(struct ct_mixer *mixer, struct rsc *rsc)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	/* Set Mic-in left mixer */
	mixer_set_input_port(mixer->amixers[AMIXER_MIC*CHN_NUM], rsc);

	return 0;
}

static int mixer_set_mic_in_right(struct ct_mixer *mixer, struct rsc *rsc)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	/* Set Mic-in right mixer */
	mixer_set_input_port(mixer->amixers[AMIXER_MIC*CHN_NUM+1], rsc);

	return 0;
}

static int mixer_set_spdif_in_left(struct ct_mixer *mixer, struct rsc *rsc)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	/* Set S/PDIF-in left mixer */
	mixer_set_input_port(mixer->amixers[AMIXER_SPDIFI*CHN_NUM], rsc);

	return 0;
}

static int mixer_set_spdif_in_right(struct ct_mixer *mixer, struct rsc *rsc)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	/* Set S/PDIF-in right mixer */
	mixer_set_input_port(mixer->amixers[AMIXER_SPDIFI*CHN_NUM+1], rsc);

	return 0;
}

static int mixer_set_pcm_in_left(struct ct_mixer *mixer, struct rsc *rsc)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	/* Set PCM-in left mixer */
	mixer_set_input_port(mixer->amixers[AMIXER_PCM*CHN_NUM], rsc);

	return 0;
}

static int mixer_set_pcm_in_right(struct ct_mixer *mixer, struct rsc *rsc)
{
	CTDPF("%s(%d) is called\n", __func__, __LINE__);

	/* Set PCM-in right mixer */
	mixer_set_input_port(mixer->amixers[AMIXER_PCM*CHN_NUM+1], rsc);

	return 0;
}

static int mixer_get_wave_out_port(struct ct_mixer *mixer, 
				   struct rsc **rleft, struct rsc **rright)
{
	if (NULL != rleft) {
		*rleft = &((amixer_t*)mixer->amixers[AMIXER_WAVE*CHN_NUM])->rsc;
	}
	if (NULL != rright) {
		*rright = &((amixer_t*)mixer->amixers[AMIXER_WAVE*CHN_NUM+1])->rsc;
	}
	return 0;
}

static int mixer_get_spdif_out_port(struct ct_mixer *mixer, 
				   struct rsc **rleft, struct rsc **rright)
{
	if (NULL != rleft) {
		*rleft = &((amixer_t*)mixer->amixers[AMIXER_SPDIFO*CHN_NUM])->rsc;
	}
	if (NULL != rright) {
		*rright = &((amixer_t*)mixer->amixers[AMIXER_SPDIFO*CHN_NUM+1])->rsc;
	}
	return 0;
}

static int mixer_get_pcm_out_port(struct ct_mixer *mixer, 
				   struct rsc **rleft, struct rsc **rright)
{
	if (NULL != rleft) {
		*rleft = &((amixer_t*)mixer->amixers[AMIXER_MASTER_C*CHN_NUM])->rsc;
	}
	if (NULL != rright) {
		*rright = &((amixer_t*)mixer->amixers[AMIXER_MASTER_C*CHN_NUM+1])->rsc;
	}
	return 0;
}

int ct_mixer_destroy(struct ct_mixer *mixer)
{
	struct sum_mgr *sum_mgr = (struct sum_mgr *)mixer->atc->rsc_mgrs[SUM];
	struct amixer_mgr *amixer_mgr = (struct amixer_mgr *)mixer->atc->rsc_mgrs[AMIXER];
	struct amixer *amixer = NULL;
	int i = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != mixer);
	CTASSERT(NULL != mixer->amixers);
	CTASSERT(NULL != mixer->sums);

	/* Release amixer resources */
	for (i = 0; i < (NUM_CT_AMIXERS * CHN_NUM); i++) {
		if (NULL != mixer->amixers[i]) {
			amixer = mixer->amixers[i];
			amixer_mgr->put_amixer(amixer_mgr, amixer);
		}
	}

	/* Release sum resources */
	for (i = 0; i < (NUM_CT_SUMS * CHN_NUM); i++) {
		if (NULL != mixer->sums[i]) {
			sum_mgr->put_sum(sum_mgr, (struct sum *)mixer->sums[i]);
		}
	}

	/* Release mem assigned to mixer object */
	kfree(mixer->sums);
	kfree(mixer->amixers);
	kfree(mixer);

	return 0;
}

int ct_mixer_create(struct ct_atc *atc, struct ct_mixer **rmixer)
{
	struct ct_mixer *mixer = NULL;
	int err = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != atc);
	CTASSERT(NULL != rmixer);

	*rmixer = NULL;

	/* Allocate mem for mixer obj */
	if ((err = ct_mixer_get_mem(&mixer))) {
		return err;
	}

	mixer->sharer_changed = mixer->switch_state = 0;
	mixer->atc = atc;
	/* Set operations */
	mixer->get_wave_out_port = mixer_get_wave_out_port;
	mixer->get_spdif_out_port = mixer_get_spdif_out_port;
	mixer->get_pcm_out_port = mixer_get_pcm_out_port;
	mixer->set_line_in_left = mixer_set_line_in_left;
	mixer->set_line_in_right = mixer_set_line_in_right;
	mixer->set_mic_in_left = mixer_set_mic_in_left;
	mixer->set_mic_in_right = mixer_set_mic_in_right;
	mixer->set_spdif_in_left = mixer_set_spdif_in_left;
	mixer->set_spdif_in_right = mixer_set_spdif_in_right;
	mixer->set_pcm_in_left = mixer_set_pcm_in_left;
	mixer->set_pcm_in_right = mixer_set_pcm_in_right;

	/* Allocate chip resources for mixer obj */
	if ((err = ct_mixer_get_resources(mixer))) {
		goto error;
	}

	/* Build internal mixer topology */
	ct_mixer_topology_build(mixer);

	*rmixer = mixer;

	return 0;

error:
	ct_mixer_destroy(mixer);
	return err;
}

int ct_alsa_mix_create(struct ct_atc *atc, 
				 enum CTALSADEVS device, 
				 const char *device_name)
{
	int err = 0;

	CTDPF("%s(%d) is called\n", __func__, __LINE__);
	CTASSERT(NULL != atc);
	CTASSERT(NULL != atc->mixer);

	/* Create snd kcontrol instances on demand */
	vol_ctl.device = swh_ctl.device = device;
	if ((err = ct_mixer_kcontrols_create((struct ct_mixer *)atc->mixer))) {
		return err;
	}

	strcpy(atc->card->mixername, device_name);

	return 0;
}
