/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	ctresource.h
 * 
 * @Brief   
 * This file contains the definition of generic hardware resources for
 * resource management.
 *
 * @Author	Liu Chun
 * @Date 	May 13 2008
 * 
 */

#ifndef CTRESOURCE_H
#define CTRESOURCE_H

#include <linux/types.h>

typedef enum {
	SRC,
	SRCIMP,
	AMIXER,
	SUM,
	DAIO,
	NUM_RSCTYP	/* This must be the last one and less than 16 */
} RSCTYP;

struct rsc_ops;
typedef struct rsc_ops rsc_ops_t;

typedef struct rsc {
	u32 idx		: 12;	/* The index of a resource */
	u32 type	: 4;	/* The type (RSCTYP) of a resource */
	u32 conj	: 12;	/* Current conjugate index */
	u32 msr		: 4;	/* The Master Sample Rate a resource working on */
	void *ctrl_blk;		/* Chip specific control info block for a resource */
	void *hw;		/* Chip specific object for hardware access means */
	struct rsc_ops *ops;	/* Generic resource operations */
} rsc_t;

struct rsc_ops {
	int (*master)(rsc_t *rsc);		/* Move to master resource */
	int (*next_conj)(rsc_t *rsc);		/* Move to next conjugate resource */
	int (*index)(const rsc_t *rsc);		/* Return the index of resource */
	int (*output_slot)(const rsc_t *rsc);	/* Return the output slot number */
};

int rsc_init(rsc_t *rsc, u32 idx, RSCTYP type, u32 msr, void *hw);
int rsc_uninit(rsc_t *rsc);

typedef struct rsc_mgr {
	RSCTYP type;		/* The type (RSCTYP) of resource to manage */
	unsigned int amount;	/* The total amount of a kind of resource */
	unsigned int avail;	/* The amount of currently available resources */
	unsigned char *rscs;	/* The bit-map for resource allocation and return */
	void *ctrl_blk;	/* Chip specific control info block for a resource manager */
	void *hw;	/* Chip specific object for hardware access means */
} rsc_mgr_t;

/* Resource management is based on bit-map mechanism */
int rsc_mgr_init(rsc_mgr_t *mgr, RSCTYP type, unsigned int amount, void *hw);
int rsc_mgr_uninit(rsc_mgr_t *mgr);
int mgr_get_resource(struct rsc_mgr *mgr, unsigned int n, unsigned int *ridx);
int mgr_put_resource(struct rsc_mgr *mgr, unsigned int n, unsigned int idx);

#endif /* CTRESOURCE_H */
