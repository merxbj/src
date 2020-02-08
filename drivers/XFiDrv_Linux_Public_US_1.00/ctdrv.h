/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @file    ctdrv.h
 * 
 * @breaf   
 * This file contains the definition of the programming interfaces that
 * provided by the driver module. 
 *
 * @author Liu Chun
 * 
 */

#ifndef CTDRV_H
#define CTDRV_H

#define PCI_VENDOR_CREATIVE		0x1102
#define PCI_DEVICE_CREATIVE_20K1	0x0005
#define PCI_DEVICE_CREATIVE_20K2	0x000B
#define PCI_SUBVENDOR_CREATIVE		0x1102
#define PCI_SUBSYS_CREATIVE_SB0760	0x0024
#define PCI_SUBSYS_CREATIVE_SB0880	0x0041
#define PCI_SUBSYS_CREATIVE_HENDRIX	0x6000

#define CT_XFI_DMA_MASK			0xffffffffUL /* 32 bits */

#endif /* CTDRV_H */
