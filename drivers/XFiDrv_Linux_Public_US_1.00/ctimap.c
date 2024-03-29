/**
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 *
 * @File	ctimap.c
 * 
 * @Brief   
 * This file contains the implementation of generic input mapper operations
 * for input mapper management.
 *
 * @Author	Liu Chun
 * @Date 	May 23 2008
 * 
 */

#include "ctimap.h"
#include "ctutils.h"
#include <linux/slab.h>

int input_mapper_add(struct list_head *mappers, struct imapper *entry,
		     int (*map_op)(void *, struct imapper *), void *data)
{
	struct list_head *pos, *pre, *head;
	struct imapper *pre_ent, *pos_ent;

	head = mappers;

	if (list_empty(head)) {
		entry->next = entry->addr;
		CTDPF("programming next=%d addr=%d user=%d slot=%d\n", entry->next, entry->addr, entry->user, entry->slot);	
		map_op(data, entry);
		list_add(&entry->list, head);
		return 0;
	}

	list_for_each(pos, head) {
		pos_ent = list_entry(pos, struct imapper, list);
		if (pos_ent->slot > entry->slot) {
			/* found a position in list */
			break;
		}
	}

	if (pos != head) {
		pre = pos->prev;
		if (pre == head) {
			pre = head->prev;
		}
		__list_add(&entry->list, pos->prev, pos);
	} else {
		pre = head->prev;
		pos = head->next;
		list_add_tail(&entry->list, head);
	}

	pre_ent = list_entry(pre, struct imapper, list);
	pos_ent = list_entry(pos, struct imapper, list);

	entry->next = pos_ent->addr;
	CTDPF("programming next=%d addr=%d user=%d slot=%d\n", entry->next, entry->addr, entry->user, entry->slot);
	map_op(data, entry);
	pre_ent->next = entry->addr;
	CTDPF("programming next=%d addr=%d user=%d slot=%d\n", pre_ent->next, pre_ent->addr, pre_ent->user, pre_ent->slot);
	map_op(data, pre_ent);
#ifdef DEBUG
	printk(KERN_ALERT "Resulting list is:\n");
	list_for_each(pos, head) {
		pos_ent = list_entry(pos, struct imapper, list);
		printk(KERN_ALERT "programming next=%d addr=%d user=%d slot=%d\n", pos_ent->next, pos_ent->addr, pos_ent->user, pos_ent->slot);
	}
#endif
	return 0;
}

int input_mapper_delete(struct list_head *mappers, struct imapper *entry,
		     int (*map_op)(void *, struct imapper *), void *data)
{
	struct list_head *next, *pre, *head;
	struct imapper *pre_ent, *next_ent;

	head = mappers;

	if (list_empty(head)) {
		return 0;
	}

	pre = (entry->list.prev == head) ? head->prev : entry->list.prev;
	next = (entry->list.next == head) ? head->next : entry->list.next;

	if (pre == &entry->list) { 
		/* entry is the only one node in mappers list */
		entry->next = entry->addr = entry->user = entry->slot = 0;
		CTDPF("programming next=%d addr=%d user=%d slot=%d\n", entry->next, entry->addr, entry->user, entry->slot);	
		map_op(data, entry);
		list_del(&entry->list);
		return 0;
	}
	
	pre_ent = list_entry(pre, struct imapper, list);
	next_ent = list_entry(next, struct imapper, list);

	pre_ent->next = next_ent->addr;
	CTDPF("programming next=%d addr=%d user=%d slot=%d\n", pre_ent->next, pre_ent->addr, pre_ent->user, pre_ent->slot);
	map_op(data, pre_ent);
	list_del(&entry->list);
#ifdef DEBUG
	printk(KERN_ALERT "Resulting list is:\n");
	list_for_each(next, head) {
		next_ent = list_entry(next, struct imapper, list);
		printk(KERN_ALERT "programming next=%d addr=%d user=%d slot=%d\n", next_ent->next, next_ent->addr, next_ent->user, next_ent->slot);
	}
#endif

	return 0;
}

void free_input_mapper_list(struct list_head *head)
{
	struct imapper *entry = NULL;
	struct list_head *pos = NULL;

	while(!list_empty(head)) {
		pos = head->next;
		list_del(pos);
		entry = list_entry(pos, struct imapper, list);
		kfree(entry);
	}
}

