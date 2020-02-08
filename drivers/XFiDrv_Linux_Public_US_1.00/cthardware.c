/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	cthardware.c
 * 
 * @Brief   
 * This file contains the implementation of hardware access methord.
 *
 * @Author	Liu Chun
 * @Date 	Jun 26 2008
 * 
 */

#include "cthardware.h"
/*#include <linux/err.h>*/

extern int create_20k2_hw_obj(hw_t **rhw);
extern int destroy_20k2_hw_obj(hw_t *hw);

extern int create_20k1_hw_obj(hw_t **rhw);
extern int destroy_20k1_hw_obj(hw_t *hw);

static enum CHIPTYP get_chip_type(struct hw *hw)
{
	enum CHIPTYP type = ATCNONE;

	switch (hw->pci->device) {
	case 0x0005:	/* 20k1 device */
		type = ATC20K1;
		break;
	case 0x000B:	/* 20k2 device */
		type = ATC20K2;
		break;
	default:
		type = ATCNONE;
		break;
	}

	return type;
}

int create_hw_obj(struct pci_dev *pci, hw_t **rhw)
{
	int err = 0;

	switch (pci->device) {
	case 0x0005:	/* 20k1 device */
		err = create_20k1_hw_obj(rhw);
		break;
	case 0x000B:	/* 20k2 device */
		err = create_20k2_hw_obj(rhw);
		break;
	default:
		err = -ENODEV;
		break;
	}
	if (err) {
		return err;
	}

	(*rhw)->pci = pci;
	(*rhw)->get_chip_type = get_chip_type;

	return 0;
}

int destroy_hw_obj(hw_t *hw)
{
	int err = 0;

	switch (hw->pci->device) {
	case 0x0005:	/* 20k1 device */
		err = destroy_20k1_hw_obj(hw);
		break;
	case 0x000B:	/* 20k2 device */
		err = destroy_20k2_hw_obj(hw);
		break;
	default:
		err = -ENODEV;
		break;
	}

	return err;
}
