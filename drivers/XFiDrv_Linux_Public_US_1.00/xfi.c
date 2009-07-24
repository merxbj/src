/*
 * xfi linux driver.
 *
 * Copyright (C) 2008, Creative Technology Ltd. All Rights Reserved.
 *
 * This source file is released under GPL v2 license (no other versions).
 * See the COPYING file included in the main directory of this source
 * distribution for the license terms and conditions.
 */

#include <linux/init.h>
#include <linux/pci.h>
#include <linux/moduleparam.h>
#include <sound/driver.h>
#include <sound/core.h>
#include <sound/initval.h>
#include "ctatc.h"
#include "ctutils.h"
#include "ctdrv.h"

MODULE_AUTHOR("Creative Technology Ltd");
MODULE_DESCRIPTION("X-Fi driver version 1.00");
MODULE_LICENSE("GPL");
MODULE_SUPPORTED_DEVICE("{{Creative Labs, Sound Blaster X-Fi}");

static int index[SNDRV_CARDS] = SNDRV_DEFAULT_IDX;
static char *id[SNDRV_CARDS] = SNDRV_DEFAULT_STR;
static int enable[SNDRV_CARDS] = SNDRV_DEFAULT_ENABLE_PNP;

static struct pci_device_id ct_pci_dev_ids[] = {
	/* only X-Fi is supported, so... */
	{ PCI_VENDOR_CREATIVE, PCI_DEVICE_CREATIVE_20K1, PCI_ANY_ID, PCI_ANY_ID, 0, 0, 0 },
	{ PCI_VENDOR_CREATIVE, PCI_DEVICE_CREATIVE_20K2, PCI_ANY_ID, PCI_ANY_ID, 0, 0, 0 },
	{ 0, }
};
MODULE_DEVICE_TABLE(pci, ct_pci_dev_ids);

static int __devinit 
ct_card_probe(struct pci_dev *pci, const struct pci_device_id *pci_id)
{
	static int dev = 0;
	struct snd_card *card;
	struct ct_atc *atc;
	int err;

	CTDPF("%s is called\n", __func__);
	if (dev >= SNDRV_CARDS) {
		return -ENODEV;
	}
	if (!enable[dev]) {
		dev++;
		return -ENOENT;
	}

	card = snd_card_new(index[dev], id[dev], THIS_MODULE, 0);
	if (card == NULL) {
		return -ENOMEM;
	}

	CTDPF("%s: Initializing devices\n", __func__);
	if ((err = ct_atc_create(card, pci, &atc)) < 0) {
		goto error;
	}
	
	CTDPF("%s: Initializing devices done\n", __func__);
	card->private_data = atc;

	/* Create alsa devices supported by this card */
	if ((err = atc->create_alsa_devs(atc)) < 0) {
		goto error;
	}

	strcpy(card->driver, "CTALSA");
	strcpy(card->shortname, "Creative X-Fi");
	strcpy(card->longname, "Creative ALSA Driver X-Fi");

	if ((err = snd_card_register(card)) < 0) {
		goto error;
	}

	pci_set_drvdata(pci, card);
	dev++;

	return 0;

error:
	snd_card_free(card);
	return err;
}

static void __devexit ct_card_remove(struct pci_dev *pci)
{
	CTDPF("%s is called\n", __func__);
	snd_card_free(pci_get_drvdata(pci));
	pci_set_drvdata(pci, NULL);
}

static struct pci_driver ct_driver = {
	.name = "CTALSA",
	.id_table = ct_pci_dev_ids,
	.probe = ct_card_probe,
	.remove = __devexit_p(ct_card_remove),
};

static int __init ct_card_init(void)
{
	CTDPF("%s is called\n", __func__);
	return pci_register_driver(&ct_driver);
}

static void __exit ct_card_exit(void)
{
	CTDPF("%s is called\n", __func__);
	pci_unregister_driver(&ct_driver);
}

module_init(ct_card_init)
module_exit(ct_card_exit)

