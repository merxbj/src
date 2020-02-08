/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	ctamixer.h
 * 
 * @Brief   
 * This file contains the definition of the Audio Mixer 
 * resource management object. 
 *
 * @Author	Liu Chun
 * @Date 	May 21 2008
 * 
 */

#ifndef CTAMIXER_H
#define CTAMIXER_H

#include "ctresource.h"
#include <linux/spinlock.h>

/* Define the descriptor of a summation node resource */
typedef struct sum {
	rsc_t rsc;		/* Basic resource info */
	unsigned char idx[8];
} sum_t;

/* Define sum resource request description info */
typedef struct sum_desc {
	unsigned int msr;
} sum_desc_t;

typedef struct sum_mgr {
	rsc_mgr_t mgr;	/* Basic resource manager info */
	spinlock_t mgr_lock;

	 /* request one sum resource */
	int (*get_sum)(struct sum_mgr *mgr, const sum_desc_t *desc, sum_t **rsum);
	/* return one sum resource */
	int (*put_sum)(struct sum_mgr *mgr, sum_t *sum);
} sum_mgr_t;

/* Constructor and destructor of daio resource manager */
int sum_mgr_create(void *hw, sum_mgr_t **rsum_mgr);
int sum_mgr_destroy(sum_mgr_t *sum_mgr);

/* Define the descriptor of a amixer resource */
struct amixer_rsc_ops;
typedef struct amixer_rsc_ops amixer_rsc_ops_t;

typedef struct amixer {
	rsc_t rsc;		/* Basic resource info */
	unsigned char idx[8];
	rsc_t *input;		/* pointer to a resource acting as input to this */
	sum_t *sum;		/* Put amixer output to this summation resource */
	amixer_rsc_ops_t *ops;	/* AMixer specific operations */
} amixer_t;

struct amixer_rsc_ops {
	int (*set_input)(amixer_t *amixer, rsc_t *rsc);
	int (*set_scale)(amixer_t *amixer, unsigned int scale);
	int (*set_invalid_squash)(amixer_t *amixer, unsigned int iv);
	int (*set_sum)(amixer_t *amixer, sum_t *sum);
	int (*commit_write)(amixer_t *amixer);
	int (*commit_raw_write)(amixer_t *amixer); /* Only for interleaved recording */
	int (*setup)(amixer_t *amixer, rsc_t *input, unsigned int scale, sum_t *sum);
	int (*get_scale)(amixer_t *amixer);
};

/* Define amixer resource request description info */
typedef struct amixer_desc {
	unsigned int msr;
} amixer_desc_t;

typedef struct amixer_mgr {
	rsc_mgr_t mgr;	/* Basic resource manager info */
	spinlock_t mgr_lock;

	 /* request one amixer resource */
	int (*get_amixer)(struct amixer_mgr *mgr, 
			  const amixer_desc_t *desc, amixer_t **ramixer);
	/* return one amixer resource */
	int (*put_amixer)(struct amixer_mgr *mgr, amixer_t *amixer);
} amixer_mgr_t;

/* Constructor and destructor of amixer resource manager */
int amixer_mgr_create(void *hw, amixer_mgr_t **ramixer_mgr);
int amixer_mgr_destroy(amixer_mgr_t *amixer_mgr);

#endif /* CTAMIXER_H */
