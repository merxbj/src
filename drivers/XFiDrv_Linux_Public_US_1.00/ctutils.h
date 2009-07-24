/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @file    ctutils.h
 * 
 * @breaf   
 * This file contains the definition of utility functions.
 *
 * @author Liu Chun
 * 
 */

#ifndef CTUTILS_H
#define CTUTILS_H

/*
 * Macros to help debugging
 */

#undef CTDPF             /* undef it, just in case */
#ifdef DEBUG
#  ifdef __KERNEL__
     /* This one if debugging is on, and kernel space */
#    include <linux/kernel.h>
#    define CTDPF(fmt, args...) printk(KERN_ALERT "(" __FILE__ "): " fmt, ## args)
#  else
     /* This one for user space */
#    include <stdio.h>
#    define CTDPF(fmt, args...) fprintf(stderr, fmt, ## args)
#  endif
#else
#  define CTDPF(fmt, args...) /* not debugging: nothing */
#endif

#undef CTDPFG
#define CTDPFG(fmt, args...) /* nothing: it's a placeholder */

#undef CTASSERT
#ifdef DEBUG
#	ifdef __KERNEL__
#		include <linux/kernel.h>
#		define CTASSERT(expr) \
			if(!(expr)) {                                  \
			printk(KERN_ALERT "Assertion failed! %s,%s,%s, \
			line=%d\n", #expr,__FILE__,__func__,__LINE__); \
			return -1; \
			}
#	endif /* __KERNEL__ */
#else /* DEBUG */
#	define CTASSERT(expr)
#endif /* DEBUG */

#endif /* CTUTILS_H */

