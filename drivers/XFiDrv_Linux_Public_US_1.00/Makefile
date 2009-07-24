ctxfi-objs := xfi.o ctatc.o ctvmem.o ctpcm.o ctmixer.o ctresource.o ctsrc.o ctamixer.o ctdaio.o ctimap.o cthardware.o cthw20k2.o cthw20k1.o
        
obj-m += ctxfi.o

ifeq ($(DEBUG), y)
CFLAGS += -g -DDEBUG
endif

KERNELDIR 	?= /lib/modules/$(shell uname -r)/build
PWD		:= $(shell pwd)

all:
	$(MAKE) -C $(KERNELDIR) M=$(PWD)

clean:
	rm -rf *.o *~ core .depend .*.cmd *.ko *.mod.c .tmp_versions

PHONY := install uninstall

MODDIR = $(PWD)
MODPATH	= /lib/modules/`uname -r`/kernel/drivers/ssound
MODULES = ctxfi.ko

install:
	@echo "Copy module files..."
	@rm -rf $(MODPATH)
	@mkdir $(MODPATH)
	@cd $(MODDIR)/; cp -f $(MODULES) $(MODPATH)/
	@echo "Update module dependency relationships..."
	@/sbin/depmod
	@/sbin/modprobe ctxfi

users := $(word 3, $(shell /sbin/lsmod | grep ^ctxfi | head -n 1))
uninstall:
	@if [ "$(users)" = "0" ]; then \
		echo "Unload ctxfi..."; \
		/sbin/modprobe -r ctxfi; \
	fi
	@echo "Remove module files..."
	@rm -rf $(MODPATH)
	@echo "Update module dependency relationships..."
	@/sbin/depmod

.PHONY: $(PHONY)
