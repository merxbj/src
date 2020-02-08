/* 
   PC Emulator

   Copyright (c) 2011 Fabrice Bellard

   Redistribution or commercial use is prohibited without the author's
   permission.
*/
"use strict";
var aa = [1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1];
var ba = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14];
var ca = [0, 1, 2, 3, 4, 5, 6, 7, 8, 0, 1, 2, 3, 4, 5, 6, 7, 8, 0, 1, 2, 3, 4, 5, 6, 7, 8, 0, 1, 2, 3, 4];

function CPU_X86() {
    var i, da;
    this.regs = new Array();
    for (i = 0; i < 8; i++) this.regs[i] = 0;
    this.eip = 0;
    this.cc_op = 0;
    this.cc_dst = 0;
    this.cc_src = 0;
    this.cc_op2 = 0;
    this.cc_dst2 = 0;
    this.df = 1;
    this.eflags = 0x2;
    this.cycle_count = 0;
    this.hard_irq = 0;
    this.hard_intno = -1;
    this.cpl = 0;
    this.cr0 = (1 << 0);
    this.cr2 = 0;
    this.cr3 = 0;
    this.cr4 = 0;
    this.idt = {
        base: 0,
        limit: 0
    };
    this.gdt = {
        base: 0,
        limit: 0
    };
    this.segs = new Array();
    for (i = 0; i < 6; i++) {
        this.segs[i] = {
            selector: 0,
            base: 0,
            limit: 0,
            flags: 0
        };
    }
    this.tr = {
        selector: 0,
        base: 0,
        limit: 0,
        flags: 0
    };
    this.ldt = {
        selector: 0,
        base: 0,
        limit: 0,
        flags: 0
    };
    this.halted = 0;
    this.phys_mem = null;
    da = 0x100000;
    this.tlb_read_kernel = new Array();
    this.tlb_write_kernel = new Array();
    this.tlb_read_user = new Array();
    this.tlb_write_user = new Array();
    for (i = 0; i < da; i++) {
        this.tlb_read_kernel[i] = -1;
        this.tlb_write_kernel[i] = -1;
        this.tlb_read_user[i] = -1;
        this.tlb_write_user[i] = -1;
    }
    this.tlb_pages = new Array();
    for (i = 0; i < 2048; i++) this.tlb_pages[i] = 0;
    this.tlb_pages_count = 0;
}
CPU_X86.prototype.phys_mem_resize = function (ea) {
    this.mem_size = ea;
    ea += ((15 + 3) & ~3);
    var i, fa, ga, ha;
    this.phys_mem8 = null;
    ha = document.getElementById("dummy_canvas");
    if (ha && ha.getContext) {
        ga = ha.getContext("2d");
        if (ga && ga.createImageData) {
            this.phys_mem8 = ga.createImageData(1024, (ea + 4095) >> 12).data;
        }
    }
    if (!this.phys_mem8) {
        fa = this.phys_mem8 = new Array();
        for (i = 0; i < ea; i++) fa[i] = 0;
    }
};
CPU_X86.prototype.ld8_phys = function (ia) {
    return this.phys_mem8[ia];
};
CPU_X86.prototype.st8_phys = function (ia, ja) {
    this.phys_mem8[ia] = ja & 0xff;
};
CPU_X86.prototype.ld32_phys = function (ia) {
    return this.phys_mem8[ia] | (this.phys_mem8[ia + 1] << 8) | (this.phys_mem8[ia + 2] << 16) | (this.phys_mem8[ia + 3] << 24);
};
CPU_X86.prototype.st32_phys = function (ia, ja) {
    this.phys_mem8[ia] = ja & 0xff;
    this.phys_mem8[ia + 1] = (ja >> 8) & 0xff;
    this.phys_mem8[ia + 2] = (ja >> 16) & 0xff;
    this.phys_mem8[ia + 3] = (ja >> 24) & 0xff;
};
CPU_X86.prototype.tlb_set_page = function (ia, ka, la, ma) {
    var i, ja, j;
    ka &= -4096;
    ia &= -4096;
    ja = ia ^ ka;
    i = ia >>> 12;
    if (this.tlb_read_kernel[i] == -1) {
        if (this.tlb_pages_count >= 2048) {
            this.tlb_flush_all1((i - 1) & 0xfffff);
        }
        this.tlb_pages[this.tlb_pages_count++] = i;
    }
    this.tlb_read_kernel[i] = ja;
    if (la) {
        this.tlb_write_kernel[i] = ja;
    } else {
        this.tlb_write_kernel[i] = -1;
    }
    if (ma) {
        this.tlb_read_user[i] = ja;
        if (la) {
            this.tlb_write_user[i] = ja;
        } else {
            this.tlb_write_user[i] = -1;
        }
    } else {
        this.tlb_read_user[i] = -1;
        this.tlb_write_user[i] = -1;
    }
};
CPU_X86.prototype.tlb_flush_page = function (ia) {
    var i;
    i = ia >>> 12;
    this.tlb_read_kernel[i] = -1;
    this.tlb_write_kernel[i] = -1;
    this.tlb_read_user[i] = -1;
    this.tlb_write_user[i] = -1;
};
CPU_X86.prototype.tlb_flush_all = function () {
    var i, j, n, na;
    na = this.tlb_pages;
    n = this.tlb_pages_count;
    for (j = 0; j < n; j++) {
        i = na[j];
        this.tlb_read_kernel[i] = -1;
        this.tlb_write_kernel[i] = -1;
        this.tlb_read_user[i] = -1;
        this.tlb_write_user[i] = -1;
    }
    this.tlb_pages_count = 0;
};
CPU_X86.prototype.tlb_flush_all1 = function (oa) {
    var i, j, n, na, pa;
    na = this.tlb_pages;
    n = this.tlb_pages_count;
    pa = 0;
    for (j = 0; j < n; j++) {
        i = na[j];
        if (i == oa) {
            na[pa++] = i;
        } else {
            this.tlb_read_kernel[i] = -1;
            this.tlb_write_kernel[i] = -1;
            this.tlb_read_user[i] = -1;
            this.tlb_write_user[i] = -1;
        }
    }
    this.tlb_pages_count = pa;
};
CPU_X86.prototype.write_string = function (ia, qa) {
    var i;
    for (i = 0; i < qa.length; i++) {
        this.st8_phys(ia++, qa.charCodeAt(i) & 0xff);
    }
    this.st8_phys(ia, 0);
};

function ra(ja, n) {
    var i, s;
    var h = "0123456789ABCDEF";
    s = "";
    for (i = n - 1; i >= 0; i--) {
        s = s + h[(ja >>> (i * 4)) & 15];
    }
    return s;
}
function sa(n) {
    return ra(n, 8);
}
function ta(n) {
    return ra(n, 2);
}
function ua(n) {
    return ra(n, 4);
}
CPU_X86.prototype.dump = function () {
    var i, va, qa;
    var wa = [" ES", " CS", " SS", " DS", " FS", " GS", "LDT", " TR"];
    console.log("TSC=" + sa(this.cycle_count) + " EIP=" + sa(this.eip) + "\nEAX=" + sa(this.regs[0]) + " ECX=" + sa(this.regs[1]) + " EDX=" + sa(this.regs[2]) + " EBX=" + sa(this.regs[3]) + " ESP=" + sa(this.regs[4]) + " EBP=" + sa(this.regs[5]));
    console.log("ESI=" + sa(this.regs[6]) + " EDI=" + sa(this.regs[7]));
    console.log("EFL=" + sa(this.eflags) + " OP=" + ta(this.cc_op) + " SRC=" + sa(this.cc_src) + " DST=" + sa(this.cc_dst) + " OP2=" + ta(this.cc_op2) + " DST2=" + sa(this.cc_dst2));
    console.log("CPL=" + this.cpl + " CR0=" + sa(this.cr0) + " CR2=" + sa(this.cr2) + " CR3=" + sa(this.cr3) + " CR4=" + sa(this.cr4));
    qa = "";
    for (i = 0; i < 8; i++) {
        if (i == 6) va = this.ldt;
        else if (i == 7) va = this.tr;
        else va = this.segs[i];
        qa += wa[i] + "=" + ua(va.selector) + " " + sa(va.base) + " " + sa(va.limit) + " " + ua((va.flags >> 8) & 0xf0ff);
        if (i & 1) {
            console.log(qa);
            qa = "";
        } else {
            qa += " ";
        }
    }
    va = this.gdt;
    qa = "GDT=     " + sa(va.base) + " " + sa(va.limit) + "      ";
    va = this.idt;
    qa += "IDT=     " + sa(va.base) + " " + sa(va.limit);
    console.log(qa);
};
CPU_X86.prototype.exec_internal = function (xa, ya) {
    var za, ia, Aa;
    var Ba, Ca, Da, Ea, Fa;
    var Ga, Ha, Ia, b, Ja, ja, Ka, La, Ma, Na, Oa, Pa;
    var Qa, Ra;
    var Sa, Ta, Ua, Va, Wa, Xa;

    function Ya() {
        var Za;
        ab(ia, 0, za.cpl == 3);
        Za = Wa[ia >>> 12] ^ ia;
        return Qa[Za];
    }
    function bb() {
        var Ra;
        return (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
    }
    function cb() {
        var ja;
        ja = bb();
        ia++;
        ja |= bb() << 8;
        ia--;
        return ja;
    }
    function db() {
        var Ra;
        return (((Ra = Wa[ia >>> 12]) | ia) & 1 ? cb() : (Ra ^= ia, Qa[Ra] | (Qa[Ra + 1] << 8)));
    }
    function eb() {
        var ja;
        ja = bb();
        ia++;
        ja |= bb() << 8;
        ia++;
        ja |= bb() << 16;
        ia++;
        ja |= bb() << 24;
        ia -= 3;
        return ja;
    }
    function fb() {
        var Ra;
        return (((Ra = Wa[ia >>> 12]) | ia) & 3 ? eb() : (Ra ^= ia, Qa[Ra] | (Qa[Ra + 1] << 8) | (Qa[Ra + 2] << 16) | (Qa[Ra + 3] << 24)));
    }
    function gb() {
        var Za;
        ab(ia, 1, za.cpl == 3);
        Za = Xa[ia >>> 12] ^ ia;
        return Qa[Za];
    }
    function hb() {
        var Za;
        return ((Za = Xa[ia >>> 12]) == -1) ? gb() : Qa[ia ^ Za];
    }
    function ib() {
        var ja;
        ja = hb();
        ia++;
        ja |= hb() << 8;
        ia--;
        return ja;
    }
    function jb() {
        var Za;
        return ((Za = Xa[ia >>> 12]) | ia) & 1 ? ib() : (Za ^= ia, Qa[Za] | (Qa[Za + 1] << 8));
    }
    function kb() {
        var ja;
        ja = hb();
        ia++;
        ja |= hb() << 8;
        ia++;
        ja |= hb() << 16;
        ia++;
        ja |= hb() << 24;
        ia -= 3;
        return ja;
    }
    function lb() {
        var Za;
        return ((Za = Xa[ia >>> 12]) | ia) & 3 ? kb() : (Za ^= ia, Qa[Za] | (Qa[Za + 1] << 8) | (Qa[Za + 2] << 16) | (Qa[Za + 3] << 24));
    }
    function mb(ja) {
        var Za;
        ab(ia, 1, za.cpl == 3);
        Za = Xa[ia >>> 12] ^ ia;
        Qa[Za] = ja & 0xff;
    }
    function nb(ja) {
        var Ra; {
            Ra = Xa[ia >>> 12];
            if (Ra == -1) {
                mb(ja);
            } else {
                Qa[ia ^ Ra] = ja & 0xff;
            }
        };
    }
    function ob(ja) {
        nb(ja);
        ia++;
        nb(ja >> 8);
        ia--;
    }
    function pb(ja) {
        var Ra; {
            Ra = Xa[ia >>> 12];
            if ((Ra | ia) & 1) {
                ob(ja);
            } else {
                Ra ^= ia;
                Qa[Ra] = ja & 0xff;
                Qa[Ra + 1] = (ja >> 8) & 0xff;
            }
        };
    }
    function qb(ja) {
        nb(ja);
        ia++;
        nb(ja >> 8);
        ia++;
        nb(ja >> 16);
        ia++;
        nb(ja >> 24);
        ia -= 3;
    }
    function rb(ja) {
        var Ra; {
            Ra = Xa[ia >>> 12];
            if ((Ra | ia) & 3) {
                qb(ja);
            } else {
                Ra ^= ia;
                Qa[Ra] = ja & 0xff;
                Qa[Ra + 1] = (ja >> 8) & 0xff;
                Qa[Ra + 2] = (ja >> 16) & 0xff;
                Qa[Ra + 3] = (ja >> 24) & 0xff;
            }
        };
    }
    function sb() {
        var Za;
        ab(ia, 0, 0);
        Za = Sa[ia >>> 12] ^ ia;
        return Qa[Za];
    }
    function tb() {
        var Za;
        return ((Za = Sa[ia >>> 12]) == -1) ? sb() : Qa[ia ^ Za];
    }
    function ub() {
        var ja;
        ja = tb();
        ia++;
        ja |= tb() << 8;
        ia--;
        return ja;
    }
    function vb() {
        var Za;
        return ((Za = Sa[ia >>> 12]) | ia) & 1 ? ub() : (Za ^= ia, Qa[Za] | (Qa[Za + 1] << 8));
    }
    function wb() {
        var ja;
        ja = tb();
        ia++;
        ja |= tb() << 8;
        ia++;
        ja |= tb() << 16;
        ia++;
        ja |= tb() << 24;
        ia -= 3;
        return ja;
    }
    function xb() {
        var Za;
        return ((Za = Sa[ia >>> 12]) | ia) & 3 ? wb() : (Za ^= ia, Qa[Za] | (Qa[Za + 1] << 8) | (Qa[Za + 2] << 16) | (Qa[Za + 3] << 24));
    }
    function yb(ja) {
        var Za;
        ab(ia, 1, 0);
        Za = Ta[ia >>> 12] ^ ia;
        Qa[Za] = ja & 0xff;
    }
    function zb(ja) {
        var Za;
        Za = Ta[ia >>> 12];
        if (Za == -1) {
            yb(ja);
        } else {
            Qa[ia ^ Za] = ja & 0xff;
        }
    }
    function Ab(ja) {
        zb(ja);
        ia++;
        zb(ja >> 8);
        ia--;
    }
    function Bb(ja) {
        var Za;
        Za = Ta[ia >>> 12];
        if ((Za | ia) & 1) {
            Ab(ja);
        } else {
            Za ^= ia;
            Qa[Za] = ja & 0xff;
            Qa[Za + 1] = (ja >> 8) & 0xff;
        }
    }
    function Cb(ja) {
        zb(ja);
        ia++;
        zb(ja >> 8);
        ia++;
        zb(ja >> 16);
        ia++;
        zb(ja >> 24);
        ia -= 3;
    }
    function Db(ja) {
        var Za;
        Za = Ta[ia >>> 12];
        if ((Za | ia) & 3) {
            Cb(ja);
        } else {
            Za ^= ia;
            Qa[Za] = ja & 0xff;
            Qa[Za + 1] = (ja >> 8) & 0xff;
            Qa[Za + 2] = (ja >> 16) & 0xff;
            Qa[Za + 3] = (ja >> 24) & 0xff;
        }
    }
    var Eb, Fb, Gb, Hb;

    function Ib() {
        var ja, Ka;
        ja = Qa[Fb++];;
        Ka = Qa[Fb++];;
        return ja | (Ka << 8);
    }
    function Jb(Ha, Kb) {
        var base, ia, Lb, Mb;
        switch ((Ha & 7) | ((Ha >> 3) & 0x18)) {
        case 0x04:
            Lb = Qa[Fb++];;
            base = Lb & 7;
            if (base == 5) {
                {
                    ia = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                    Fb += 4;
                };
            } else {
                ia = Aa[base];
                if (Kb && base == 4) ia = (ia + Kb) & -1;
            }
            Mb = (Lb >> 3) & 7;
            if (Mb != 4) {
                ia = (ia + (Aa[Mb] << (Lb >> 6))) & -1;
            }
            break;
        case 0x0c:
            Lb = Qa[Fb++];;
            ia = ((Qa[Fb++] << 24) >> 24);;
            base = Lb & 7;
            ia = (ia + Aa[base]) & -1;
            if (Kb && base == 4) ia = (ia + Kb) & -1;
            Mb = (Lb >> 3) & 7;
            if (Mb != 4) {
                ia = (ia + (Aa[Mb] << (Lb >> 6))) & -1;
            }
            break;
        case 0x14:
            Lb = Qa[Fb++];; {
                ia = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                Fb += 4;
            };
            base = Lb & 7;
            ia = (ia + Aa[base]) & -1;
            if (Kb && base == 4) ia = (ia + Kb) & -1;
            Mb = (Lb >> 3) & 7;
            if (Mb != 4) {
                ia = (ia + (Aa[Mb] << (Lb >> 6))) & -1;
            }
            break;
        case 0x05:
            {
                ia = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                Fb += 4;
            };
            break;
        case 0x00:
        case 0x01:
        case 0x02:
        case 0x03:
        case 0x06:
        case 0x07:
            base = Ha & 7;
            ia = Aa[base];
            break;
        case 0x08:
        case 0x09:
        case 0x0a:
        case 0x0b:
        case 0x0d:
        case 0x0e:
        case 0x0f:
            ia = ((Qa[Fb++] << 24) >> 24);;
            base = Ha & 7;
            ia = (ia + Aa[base]) & -1;
            break;
        case 0x10:
        case 0x11:
        case 0x12:
        case 0x13:
        case 0x15:
        case 0x16:
        case 0x17:
            {
                ia = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                Fb += 4;
            };
            base = Ha & 7;
            ia = (ia + Aa[base]) & -1;
            break;
        default:
            throw "get_modrm";
        }
        if (Ga & 0x000f) {
            ia = (ia + za.segs[(Ga & 0x000f) - 1].base) & -1;
        }
        return ia;
    }
    function Nb() {
        var ia; {
            ia = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
            Fb += 4;
        };
        if (Ga & 0x000f) {
            ia = (ia + za.segs[(Ga & 0x000f) - 1].base) & -1;
        }
        return ia;
    }
    function Ob(Ja, ja) {
        if (Ja & 4) Aa[Ja & 3] = (Aa[Ja & 3] & -65281) | ((ja & 0xff) << 8);
        else Aa[Ja & 3] = (Aa[Ja & 3] & -256) | (ja & 0xff);
    }
    function Pb(Ja, ja) {
        Aa[Ja] = (Aa[Ja] & -65536) | (ja & 0xffff);
    }
    function Qb(Ma, Rb, Sb) {
        var Tb;
        switch (Ma) {
        case 0:
            Ba = Sb;
            Rb = (Rb + Sb) & -1;
            Ca = Rb;
            Da = 0;
            break;
        case 1:
            Rb = Rb | Sb;
            Ca = Rb;
            Da = 12;
            break;
        case 2:
            Tb = Ub(2);
            Ba = Sb;
            Rb = (Rb + Sb + Tb) & -1;
            Ca = Rb;
            Da = Tb ? 3 : 0;
            break;
        case 3:
            Tb = Ub(2);
            Ba = Sb;
            Rb = (Rb - Sb - Tb) & -1;
            Ca = Rb;
            Da = Tb ? 9 : 6;
            break;
        case 4:
            Rb = Rb & Sb;
            Ca = Rb;
            Da = 12;
            break;
        case 5:
            Ba = Sb;
            Rb = (Rb - Sb) & -1;
            Ca = Rb;
            Da = 6;
            break;
        case 6:
            Rb = Rb ^ Sb;
            Ca = Rb;
            Da = 12;
            break;
        case 7:
            Ba = Sb;
            Ca = (Rb - Sb) & -1;
            Da = 6;
            break;
        default:
            throw "arith" + 8 + ": invalid op";
        }
        return Rb;
    }
    function Vb(ja) {
        if (Da < 25) {
            Ea = Da;
        }
        Fa = (ja + 1) & -1;
        Da = 25;
        return Fa;
    }
    function Wb(ja) {
        if (Da < 25) {
            Ea = Da;
        }
        Fa = (ja - 1) & -1;
        Da = 28;
        return Fa;
    }
    function Xb(Ma, Rb, Sb) {
        var Tb;
        switch (Ma) {
        case 0:
            Ba = Sb;
            Rb = (Rb + Sb) & -1;
            Ca = Rb;
            Da = 1;
            break;
        case 1:
            Rb = Rb | Sb;
            Ca = Rb;
            Da = 13;
            break;
        case 2:
            Tb = Ub(2);
            Ba = Sb;
            Rb = (Rb + Sb + Tb) & -1;
            Ca = Rb;
            Da = Tb ? 4 : 1;
            break;
        case 3:
            Tb = Ub(2);
            Ba = Sb;
            Rb = (Rb - Sb - Tb) & -1;
            Ca = Rb;
            Da = Tb ? 10 : 7;
            break;
        case 4:
            Rb = Rb & Sb;
            Ca = Rb;
            Da = 13;
            break;
        case 5:
            Ba = Sb;
            Rb = (Rb - Sb) & -1;
            Ca = Rb;
            Da = 7;
            break;
        case 6:
            Rb = Rb ^ Sb;
            Ca = Rb;
            Da = 13;
            break;
        case 7:
            Ba = Sb;
            Ca = (Rb - Sb) & -1;
            Da = 7;
            break;
        default:
            throw "arith" + 16 + ": invalid op";
        }
        return Rb;
    }
    function Yb(ja) {
        if (Da < 25) {
            Ea = Da;
        }
        Fa = (ja + 1) & -1;
        Da = 26;
        return Fa;
    }
    function Zb(ja) {
        if (Da < 25) {
            Ea = Da;
        }
        Fa = (ja - 1) & -1;
        Da = 29;
        return Fa;
    }
    function ac(Ma, Rb, Sb) {
        var Tb;
        switch (Ma) {
        case 0:
            Ba = Sb;
            Rb = (Rb + Sb) & -1;
            Ca = Rb;
            Da = 2;
            break;
        case 1:
            Rb = Rb | Sb;
            Ca = Rb;
            Da = 14;
            break;
        case 2:
            Tb = Ub(2);
            Ba = Sb;
            Rb = (Rb + Sb + Tb) & -1;
            Ca = Rb;
            Da = Tb ? 5 : 2;
            break;
        case 3:
            Tb = Ub(2);
            Ba = Sb;
            Rb = (Rb - Sb - Tb) & -1;
            Ca = Rb;
            Da = Tb ? 11 : 8;
            break;
        case 4:
            Rb = Rb & Sb;
            Ca = Rb;
            Da = 14;
            break;
        case 5:
            Ba = Sb;
            Rb = (Rb - Sb) & -1;
            Ca = Rb;
            Da = 8;
            break;
        case 6:
            Rb = Rb ^ Sb;
            Ca = Rb;
            Da = 14;
            break;
        case 7:
            Ba = Sb;
            Ca = (Rb - Sb) & -1;
            Da = 8;
            break;
        default:
            throw "arith" + 32 + ": invalid op";
        }
        return Rb;
    }
    function bc(ja) {
        if (Da < 25) {
            Ea = Da;
        }
        Fa = (ja + 1) & -1;
        Da = 27;
        return Fa;
    }
    function cc(ja) {
        if (Da < 25) {
            Ea = Da;
        }
        Fa = (ja - 1) & -1;
        Da = 30;
        return Fa;
    }
    function dc(Ma, Rb, Sb) {
        var ec, Tb;
        switch (Ma) {
        case 0:
            if (Sb & 0x1f) {
                Sb &= 0x7;
                Rb &= 0xff;
                ec = Rb;
                Rb = (Rb << Sb) | (Rb >>> (8 - Sb));
                Ba = fc() & ~ (0x0800 | 0x0001);
                Ba |= (Rb & 0x0001) | (((ec ^ Rb) << 4) & 0x0800);
                Da = 24;
            }
            break;
        case 1:
            if (Sb & 0x1f) {
                Sb &= 0x7;
                Rb &= 0xff;
                ec = Rb;
                Rb = (Rb >>> Sb) | (Rb << (8 - Sb));
                Ba = fc() & ~ (0x0800 | 0x0001);
                Ba |= ((Rb >> 7) & 0x0001) | (((ec ^ Rb) << 4) & 0x0800);
                Da = 24;
            }
            break;
        case 2:
            Sb = ca[Sb & 0x1f];
            if (Sb) {
                Rb &= 0xff;
                ec = Rb;
                Tb = Ub(2);
                Rb = (Rb << Sb) | (Tb << (Sb - 1));
                if (Sb > 1) Rb |= ec >>> (9 - Sb);
                Ba = fc() & ~ (0x0800 | 0x0001);
                Ba |= (((ec ^ Rb) << 4) & 0x0800) | ((ec >> (8 - Sb)) & 0x0001);
                Da = 24;
            }
            break;
        case 3:
            Sb = ca[Sb & 0x1f];
            if (Sb) {
                Rb &= 0xff;
                ec = Rb;
                Tb = Ub(2);
                Rb = (Rb >>> Sb) | (Tb << (8 - Sb));
                if (Sb > 1) Rb |= ec << (9 - Sb);
                Ba = fc() & ~ (0x0800 | 0x0001);
                Ba |= (((ec ^ Rb) << 4) & 0x0800) | ((ec >> (Sb - 1)) & 0x0001);
                Da = 24;
            }
            break;
        case 4:
        case 6:
            Sb &= 0x1f;
            if (Sb) {
                Ba = Rb << (Sb - 1);
                Ca = Rb = Rb << Sb;
                Da = 15;
            }
            break;
        case 5:
            Sb &= 0x1f;
            if (Sb) {
                Rb &= 0xff;
                Ba = Rb >>> (Sb - 1);
                Ca = Rb = Rb >>> Sb;
                Da = 18;
            }
            break;
        case 7:
            Sb &= 0x1f;
            if (Sb) {
                Rb = (Rb << 24) >> 24;
                Ba = Rb >> (Sb - 1);
                Ca = Rb = Rb >> Sb;
                Da = 18;
            }
            break;
        default:
            throw "unsupported shift8=" + Ma;
        }
        return Rb;
    }
    function gc(Ma, Rb, Sb) {
        var ec, Tb;
        switch (Ma) {
        case 0:
            if (Sb & 0x1f) {
                Sb &= 0xf;
                Rb &= 0xffff;
                ec = Rb;
                Rb = (Rb << Sb) | (Rb >>> (16 - Sb));
                Ba = fc() & ~ (0x0800 | 0x0001);
                Ba |= (Rb & 0x0001) | (((ec ^ Rb) >> 4) & 0x0800);
                Da = 24;
            }
            break;
        case 1:
            if (Sb & 0x1f) {
                Sb &= 0xf;
                Rb &= 0xffff;
                ec = Rb;
                Rb = (Rb >>> Sb) | (Rb << (16 - Sb));
                Ba = fc() & ~ (0x0800 | 0x0001);
                Ba |= ((Rb >> 15) & 0x0001) | (((ec ^ Rb) >> 4) & 0x0800);
                Da = 24;
            }
            break;
        case 2:
            Sb = ba[Sb & 0x1f];
            if (Sb) {
                Rb &= 0xffff;
                ec = Rb;
                Tb = Ub(2);
                Rb = (Rb << Sb) | (Tb << (Sb - 1));
                if (Sb > 1) Rb |= ec >>> (17 - Sb);
                Ba = fc() & ~ (0x0800 | 0x0001);
                Ba |= (((ec ^ Rb) >> 4) & 0x0800) | ((ec >> (16 - Sb)) & 0x0001);
                Da = 24;
            }
            break;
        case 3:
            Sb = ba[Sb & 0x1f];
            if (Sb) {
                Rb &= 0xffff;
                ec = Rb;
                Tb = Ub(2);
                Rb = (Rb >>> Sb) | (Tb << (16 - Sb));
                if (Sb > 1) Rb |= ec << (17 - Sb);
                Ba = fc() & ~ (0x0800 | 0x0001);
                Ba |= (((ec ^ Rb) >> 4) & 0x0800) | ((ec >> (Sb - 1)) & 0x0001);
                Da = 24;
            }
            break;
        case 4:
        case 6:
            Sb &= 0x1f;
            if (Sb) {
                Ba = Rb << (Sb - 1);
                Ca = Rb = Rb << Sb;
                Da = 16;
            }
            break;
        case 5:
            Sb &= 0x1f;
            if (Sb) {
                Rb &= 0xffff;
                Ba = Rb >>> (Sb - 1);
                Ca = Rb = Rb >>> Sb;
                Da = 19;
            }
            break;
        case 7:
            Sb &= 0x1f;
            if (Sb) {
                Rb = (Rb << 16) >> 16;
                Ba = Rb >> (Sb - 1);
                Ca = Rb = Rb >> Sb;
                Da = 19;
            }
            break;
        default:
            throw "unsupported shift16=" + Ma;
        }
        return Rb;
    }
    function hc(Ma, Rb, Sb) {
        var ec, Tb;
        switch (Ma) {
        case 0:
            Sb &= 0x1f;
            if (Sb) {
                ec = Rb;
                Rb = (Rb << Sb) | (Rb >>> (32 - Sb));
                Ba = fc() & ~ (0x0800 | 0x0001);
                Ba |= (Rb & 0x0001) | (((ec ^ Rb) >> 20) & 0x0800);
                Da = 24;
            }
            break;
        case 1:
            Sb &= 0x1f;
            if (Sb) {
                ec = Rb;
                Rb = (Rb >>> Sb) | (Rb << (32 - Sb));
                Ba = fc() & ~ (0x0800 | 0x0001);
                Ba |= ((Rb >> 31) & 0x0001) | (((ec ^ Rb) >> 20) & 0x0800);
                Da = 24;
            }
            break;
        case 2:
            Sb &= 0x1f;
            if (Sb) {
                ec = Rb;
                Tb = Ub(2);
                Rb = (Rb << Sb) | (Tb << (Sb - 1));
                if (Sb > 1) Rb |= ec >>> (33 - Sb);
                Ba = fc() & ~ (0x0800 | 0x0001);
                Ba |= (((ec ^ Rb) >> 20) & 0x0800) | ((ec >> (32 - Sb)) & 0x0001);
                Da = 24;
            }
            break;
        case 3:
            Sb &= 0x1f;
            if (Sb) {
                ec = Rb;
                Tb = Ub(2);
                Rb = (Rb >>> Sb) | (Tb << (32 - Sb));
                if (Sb > 1) Rb |= ec << (33 - Sb);
                Ba = fc() & ~ (0x0800 | 0x0001);
                Ba |= (((ec ^ Rb) >> 20) & 0x0800) | ((ec >> (Sb - 1)) & 0x0001);
                Da = 24;
            }
            break;
        case 4:
        case 6:
            Sb &= 0x1f;
            if (Sb) {
                Ba = Rb << (Sb - 1);
                Ca = Rb = Rb << Sb;
                Da = 17;
            }
            break;
        case 5:
            Sb &= 0x1f;
            if (Sb) {
                Ba = Rb >>> (Sb - 1);
                Ca = Rb = Rb >>> Sb;
                Da = 20;
            }
            break;
        case 7:
            Sb &= 0x1f;
            if (Sb) {
                Ba = Rb >> (Sb - 1);
                Ca = Rb = Rb >> Sb;
                Da = 20;
            }
            break;
        default:
            throw "unsupported shift32=" + Ma;
        }
        return Rb;
    }
    function ic(Rb, Sb, jc) {
        jc &= 0x1f;
        if (jc) {
            Ba = Rb << (jc - 1);
            Ca = Rb = (Rb << jc) | (Sb >>> (32 - jc));
            Da = 17;
        }
        return Rb;
    }
    function kc(Rb, Sb, jc) {
        jc &= 0x1f;
        if (jc) {
            Ba = Rb >> (jc - 1);
            Ca = Rb = (Rb >>> jc) | (Sb << (32 - jc));
            Da = 20;
        }
        return Rb;
    }
    function lc(Rb, Sb) {
        Sb &= 0x1f;
        Ba = Rb >> Sb;
        Da = 20;
    }
    function mc(Rb, Sb) {
        Sb &= 0x1f;
        Ba = Rb >> Sb;
        Rb |= (1 << Sb);
        Da = 20;
        return Rb;
    }
    function nc(Rb, Sb) {
        Sb &= 0x1f;
        Ba = Rb >> Sb;
        Rb &= ~ (1 << Sb);
        Da = 20;
        return Rb;
    }
    function oc(Rb, Sb) {
        Sb &= 0x1f;
        Ba = Rb >> Sb;
        Rb ^= (1 << Sb);
        Da = 20;
        return Rb;
    }
    function pc(Rb, Sb) {
        if (Sb) {
            Rb = 0;
            while ((Sb & 1) == 0) {
                Rb++;
                Sb >>= 1;
            }
            Ca = 1;
        } else {
            Ca = 0;
        }
        Da = 14;
        return Rb;
    }
    function qc(Rb, Sb) {
        if (Sb) {
            Rb = 31;
            while (Sb >= 0) {
                Rb--;
                Sb <<= 1;
            }
            Ca = 1;
        } else {
            Ca = 0;
        }
        Da = 14;
        return Rb;
    }
    function rc(b) {
        var a, q, r;
        a = Aa[0] & 0xffff;
        b &= 0xff;
        if ((a >> 8) >= b) sc(0);
        q = (a / b) & -1;
        r = (a % b);
        Pb(0, (q & 0xff) | (r << 8));
    }
    function tc(b) {
        var a, q, r;
        a = (Aa[0] << 16) >> 16;
        b = (b << 24) >> 24;
        if (b == 0) sc(0);
        q = (a / b) & -1;
        if (((q << 24) >> 24) != q) sc(0);
        r = (a % b);
        Pb(0, (q & 0xff) | (r << 8));
    }
    function uc(b) {
        var a, q, r;
        a = (Aa[2] << 16) | (Aa[0] & 0xffff);
        b &= 0xffff;
        if ((a >>> 16) >= b) sc(0);
        q = (a / b) & -1;
        r = (a % b);
        Pb(0, q);
        Pb(2, r);
    }
    function vc(b) {
        var a, q, r;
        a = (Aa[2] << 16) | (Aa[0] & 0xffff);
        b = (b << 16) >> 16;
        if (b == 0) sc(0);
        q = (a / b) & -1;
        if (((q << 16) >> 16) != q) sc(0);
        r = (a % b);
        Pb(0, q);
        Pb(2, r);
    }
    function wc(xc, yc, b) {
        var a, i, zc;
        xc = xc >>> 0;
        yc = yc >>> 0;
        b = b >>> 0;
        if (xc >= b) {
            sc(0);
        }
        if (xc >= 0 && xc <= 0x200000) {
            a = xc * 4294967296 + yc;
            Pa = (a % b) & -1;
            return (a / b) & -1;
        } else {
            for (i = 0; i < 32; i++) {
                zc = xc >> 31;
                xc = ((xc << 1) | (yc >>> 31)) >>> 0;
                if (zc || xc >= b) {
                    xc = xc - b;
                    yc = (yc << 1) | 1;
                } else {
                    yc = yc << 1;
                }
            }
            Pa = xc & -1;
            return yc;
        }
    }
    function Ac(xc, yc, b) {
        var Bc, Cc, q;
        if (xc < 0) {
            Bc = 1;
            xc = ~xc;
            yc = (-yc) & -1;
            if (yc == 0) xc = (xc + 1) & -1;
        } else {
            Bc = 0;
        }
        if (b < 0) {
            b = -b & -1;
            Cc = 1;
        } else {
            Cc = 0;
        }
        q = wc(xc, yc, b);
        Cc ^= Bc;
        if (Cc) {
            if ((q >>> 0) > 0x80000000) sc(0);
            q = (-q) & -1;
        } else {
            if ((q >>> 0) >= 0x80000000) sc(0);
        }
        if (Bc) {
            Pa = (-Pa) & -1;
        }
        return q;
    }
    function Dc(a, b) {
        a &= 0xff;
        b &= 0xff;
        Ca = (Aa[0] & 0xff) * (b & 0xff);
        Ba = Ca >> 8;
        Da = 21;
        return Ca;
    }
    function Ec(a, b) {
        a = (a << 24) >> 24;
        b = (b << 24) >> 24;
        Ca = (a * b) & -1;
        Ba = (Ca != ((Ca << 24) >> 24)) >> 0;
        Da = 21;
        return Ca;
    }
    function Fc(a, b) {
        Ca = ((a & 0xffff) * (b & 0xffff)) & -1;
        Ba = Ca >>> 16;
        Da = 22;
        return Ca;
    }
    function Gc(a, b) {
        a = (a << 16) >> 16;
        b = (b << 16) >> 16;
        Ca = (a * b) & -1;
        Ba = (Ca != ((Ca << 16) >> 16)) >> 0;
        Da = 22;
        return Ca;
    }
    function Hc(a, b) {
        var r, yc, xc, Ic, Jc, m;
        a = a >>> 0;
        b = b >>> 0;
        r = a * b;
        if (r <= 0xffffffff) {
            Pa = 0;
            r &= -1;
        } else {
            yc = a & 0xffff;
            xc = a >>> 16;
            Ic = b & 0xffff;
            Jc = b >>> 16;
            r = yc * Ic;
            Pa = xc * Jc;
            m = yc * Jc;
            r += (((m & 0xffff) << 16) >>> 0);
            Pa += (m >>> 16);
            if (r >= 4294967296) {
                r -= 4294967296;
                Pa++;
            }
            m = xc * Ic;
            r += (((m & 0xffff) << 16) >>> 0);
            Pa += (m >>> 16);
            if (r >= 4294967296) {
                r -= 4294967296;
                Pa++;
            }
            r &= -1;
            Pa &= -1;
        }
        return r;
    }
    function Kc(a, b) {
        Ca = Hc(a, b);
        Ba = Pa;
        Da = 23;
        return Ca;
    }
    function Lc(a, b) {
        var s, r;
        s = 0;
        if (a < 0) {
            a = -a;
            s = 1;
        }
        if (b < 0) {
            b = -b;
            s ^= 1;
        }
        r = Hc(a, b);
        if (s) {
            Pa = ~Pa;
            r = (-r) & -1;
            if (r == 0) {
                Pa = (Pa + 1) & -1;
            }
        }
        Ca = r;
        Ba = (Pa - (r >> 31)) & -1;
        Da = 23;
        return r;
    }
    function Mc(Da) {
        var Rb, Nc;
        switch (Da) {
        case 0:
            Nc = (Ca & 0xff) < (Ba & 0xff);
            break;
        case 1:
            Nc = (Ca & 0xffff) < (Ba & 0xffff);
            break;
        case 2:
            Nc = (Ca >>> 0) < (Ba >>> 0);
            break;
        case 3:
            Nc = (Ca & 0xff) <= (Ba & 0xff);
            break;
        case 4:
            Nc = (Ca & 0xffff) <= (Ba & 0xffff);
            break;
        case 5:
            Nc = (Ca >>> 0) <= (Ba >>> 0);
            break;
        case 6:
            Nc = ((Ca + Ba) & 0xff) < (Ba & 0xff);
            break;
        case 7:
            Nc = ((Ca + Ba) & 0xffff) < (Ba & 0xffff);
            break;
        case 8:
            Nc = ((Ca + Ba) >>> 0) < (Ba >>> 0);
            break;
        case 9:
            Rb = (Ca + Ba + 1) & 0xff;
            Nc = Rb <= (Ba & 0xff);
            break;
        case 10:
            Rb = (Ca + Ba + 1) & 0xffff;
            Nc = Rb <= (Ba & 0xffff);
            break;
        case 11:
            Rb = (Ca + Ba + 1) >>> 0;
            Nc = Rb <= (Ba >>> 0);
            break;
        case 12:
        case 13:
        case 14:
            Nc = 0;
            break;
        case 15:
            Nc = (Ba >> 7) & 1;
            break;
        case 16:
            Nc = (Ba >> 15) & 1;
            break;
        case 17:
            Nc = (Ba >> 31) & 1;
            break;
        case 18:
        case 19:
        case 20:
            Nc = Ba & 1;
            break;
        case 21:
        case 22:
        case 23:
            Nc = Ba != 0;
            break;
        case 24:
            Nc = Ba & 1;
            break;
        default:
            throw "GET_CARRY: unsupported cc_op=" + Da;
        }
        return Nc;
    }
    function Ub(Oc) {
        var Nc, Rb;
        switch (Oc >> 1) {
        case 0:
            switch (Da) {
            case 0:
                Rb = (Ca - Ba) & -1;
                Nc = (((Rb ^ Ba ^ -1) & (Rb ^ Ca)) >> 7) & 1;
                break;
            case 1:
                Rb = (Ca - Ba) & -1;
                Nc = (((Rb ^ Ba ^ -1) & (Rb ^ Ca)) >> 15) & 1;
                break;
            case 2:
                Rb = (Ca - Ba) & -1;
                Nc = (((Rb ^ Ba ^ -1) & (Rb ^ Ca)) >> 31) & 1;
                break;
            case 3:
                Rb = (Ca - Ba - 1) & -1;
                Nc = (((Rb ^ Ba ^ -1) & (Rb ^ Ca)) >> 7) & 1;
                break;
            case 4:
                Rb = (Ca - Ba - 1) & -1;
                Nc = (((Rb ^ Ba ^ -1) & (Rb ^ Ca)) >> 15) & 1;
                break;
            case 5:
                Rb = (Ca - Ba - 1) & -1;
                Nc = (((Rb ^ Ba ^ -1) & (Rb ^ Ca)) >> 31) & 1;
                break;
            case 6:
                Rb = (Ca + Ba) & -1;
                Nc = (((Rb ^ Ba) & (Rb ^ Ca)) >> 7) & 1;
                break;
            case 7:
                Rb = (Ca + Ba) & -1;
                Nc = (((Rb ^ Ba) & (Rb ^ Ca)) >> 15) & 1;
                break;
            case 8:
                Rb = (Ca + Ba) & -1;
                Nc = (((Rb ^ Ba) & (Rb ^ Ca)) >> 31) & 1;
                break;
            case 9:
                Rb = (Ca + Ba + 1) & -1;
                Nc = (((Rb ^ Ba) & (Rb ^ Ca)) >> 7) & 1;
                break;
            case 10:
                Rb = (Ca + Ba + 1) & -1;
                Nc = (((Rb ^ Ba) & (Rb ^ Ca)) >> 15) & 1;
                break;
            case 11:
                Rb = (Ca + Ba + 1) & -1;
                Nc = (((Rb ^ Ba) & (Rb ^ Ca)) >> 31) & 1;
                break;
            case 12:
            case 13:
            case 14:
                Nc = 0;
                break;
            case 15:
            case 18:
                Nc = ((Ba ^ Ca) >> 7) & 1;
                break;
            case 16:
            case 19:
                Nc = ((Ba ^ Ca) >> 15) & 1;
                break;
            case 17:
            case 20:
                Nc = ((Ba ^ Ca) >> 31) & 1;
                break;
            case 21:
            case 22:
            case 23:
                Nc = Ba != 0;
                break;
            case 24:
                Nc = (Ba >> 11) & 1;
                break;
            case 25:
                Nc = (Fa & 0xff) == 0x80;
                break;
            case 26:
                Nc = (Fa & 0xffff) == 0x8000;
                break;
            case 27:
                Nc = (Fa == -2147483648);
                break;
            case 28:
                Nc = (Fa & 0xff) == 0x7f;
                break;
            case 29:
                Nc = (Fa & 0xffff) == 0x7fff;
                break;
            case 30:
                Nc = Fa == 0x7fffffff;
                break;
            default:
                throw "JO: unsupported cc_op=" + Da;
            }
            break;
        case 1:
            if (Da >= 25) {
                Nc = Mc(Ea);
            } else {
                Nc = Mc(Da);
            }
            break;
        case 2:
            switch (Da) {
            case 0:
            case 3:
            case 6:
            case 9:
            case 12:
            case 15:
            case 18:
            case 21:
                Nc = (Ca & 0xff) == 0;
                break;
            case 1:
            case 4:
            case 7:
            case 10:
            case 13:
            case 16:
            case 19:
            case 22:
                Nc = (Ca & 0xffff) == 0;
                break;
            case 2:
            case 5:
            case 8:
            case 11:
            case 14:
            case 17:
            case 20:
            case 23:
                Nc = Ca == 0;
                break;
            case 24:
                Nc = (Ba >> 6) & 1;
                break;
            case 25:
            case 28:
                Nc = (Fa & 0xff) == 0;
                break;
            case 26:
            case 29:
                Nc = (Fa & 0xffff) == 0;
                break;
            case 27:
            case 30:
                Nc = Fa == 0;
                break;
            default:
                throw "JZ: unsupported cc_op=" + Da;
            };
            break;
        case 3:
            switch (Da) {
            case 6:
                Nc = ((Ca + Ba) & 0xff) <= (Ba & 0xff);
                break;
            case 7:
                Nc = ((Ca + Ba) & 0xffff) <= (Ba & 0xffff);
                break;
            case 8:
                Nc = ((Ca + Ba) >>> 0) <= (Ba >>> 0);
                break;
            case 24:
                Nc = (Ba & (0x0040 | 0x0001)) != 0;
                break;
            default:
                Nc = Ub(2) | Ub(4);
                break;
            }
            break;
        case 4:
            switch (Da) {
            case 0:
            case 3:
            case 6:
            case 9:
            case 12:
            case 15:
            case 18:
            case 21:
                Nc = (Ca >> 7) & 1;
                break;
            case 1:
            case 4:
            case 7:
            case 10:
            case 13:
            case 16:
            case 19:
            case 22:
                Nc = (Ca >> 15) & 1;
                break;
            case 2:
            case 5:
            case 8:
            case 11:
            case 14:
            case 17:
            case 20:
            case 23:
                Nc = Ca < 0;
                break;
            case 24:
                Nc = (Ba >> 7) & 1;
                break;
            case 25:
            case 28:
                Nc = (Fa >> 7) & 1;
                break;
            case 26:
            case 29:
                Nc = (Fa >> 15) & 1;
                break;
            case 27:
            case 30:
                Nc = Fa < 0;
                break;
            default:
                throw "JS: unsupported cc_op=" + Da;
            }
            break;
        case 5:
            switch (Da) {
            case 0:
            case 3:
            case 6:
            case 9:
            case 12:
            case 15:
            case 18:
            case 21:
            case 1:
            case 4:
            case 7:
            case 10:
            case 13:
            case 16:
            case 19:
            case 22:
            case 2:
            case 5:
            case 8:
            case 11:
            case 14:
            case 17:
            case 20:
            case 23:
                Nc = aa[Ca & 0xff];
                break;
            case 24:
                Nc = (Ba >> 2) & 1;
                break;
            case 25:
            case 28:
            case 26:
            case 29:
            case 27:
            case 30:
                Nc = aa[Fa & 0xff];
                break;
            default:
                throw "JP: unsupported cc_op=" + Da;
            }
            break;
        case 6:
            switch (Da) {
            case 6:
                Nc = ((Ca + Ba) << 24) < (Ba << 24);
                break;
            case 7:
                Nc = ((Ca + Ba) << 16) < (Ba << 16);
                break;
            case 8:
                Nc = ((Ca + Ba) & -1) < Ba;
                break;
            case 12:
                Nc = (Ca << 24) < 0;
                break;
            case 13:
                Nc = (Ca << 16) < 0;
                break;
            case 14:
                Nc = Ca < 0;
                break;
            case 24:
                Nc = ((Ba >> 7) ^ (Ba >> 11)) & 1;
                break;
            case 25:
            case 28:
                Nc = (Fa << 24) < 0;
                break;
            case 26:
            case 29:
                Nc = (Fa << 16) < 0;
                break;
            case 27:
            case 30:
                Nc = Fa < 0;
                break;
            default:
                Nc = Ub(8) ^ Ub(0);
                break;
            }
            break;
        case 7:
            switch (Da) {
            case 6:
                Nc = ((Ca + Ba) << 24) <= (Ba << 24);
                break;
            case 7:
                Nc = ((Ca + Ba) << 16) <= (Ba << 16);
                break;
            case 8:
                Nc = ((Ca + Ba) & -1) <= Ba;
                break;
            case 12:
                Nc = (Ca << 24) <= 0;
                break;
            case 13:
                Nc = (Ca << 16) <= 0;
                break;
            case 14:
                Nc = Ca <= 0;
                break;
            case 24:
                Nc = (((Ba >> 7) ^ (Ba >> 11)) | (Ba >> 6)) & 1;
                break;
            case 25:
            case 28:
                Nc = (Fa << 24) <= 0;
                break;
            case 26:
            case 29:
                Nc = (Fa << 16) <= 0;
                break;
            case 27:
            case 30:
                Nc = Fa <= 0;
                break;
            default:
                Nc = (Ub(8) ^ Ub(0)) | Ub(4);
                break;
            }
            break;
        default:
            throw "unsupported cond: " + Oc;
        }
        return Nc ^ (Oc & 1);
    }
    function Pc() {
        var Rb, Nc;
        switch (Da) {
        case 0:
        case 1:
        case 2:
            Rb = (Ca - Ba) & -1;
            Nc = (Ca ^ Rb ^ Ba) & 0x10;
            break;
        case 3:
        case 4:
        case 5:
            Rb = (Ca - Ba - 1) & -1;
            Nc = (Ca ^ Rb ^ Ba) & 0x10;
            break;
        case 6:
        case 7:
        case 8:
            Rb = (Ca + Ba) & -1;
            Nc = (Ca ^ Rb ^ Ba) & 0x10;
            break;
        case 9:
        case 10:
        case 11:
            Rb = (Ca + Ba + 1) & -1;
            Nc = (Ca ^ Rb ^ Ba) & 0x10;
            break;
        case 12:
        case 13:
        case 14:
            Nc = 0;
            break;
        case 15:
        case 18:
        case 16:
        case 19:
        case 17:
        case 20:
        case 21:
        case 22:
        case 23:
            Nc = 0;
            break;
        case 24:
            Nc = Ba & 0x10;
            break;
        case 25:
        case 26:
        case 27:
            Nc = (Fa ^ (Fa - 1)) & 0x10;
            break;
        case 28:
        case 29:
        case 30:
            Nc = (Fa ^ (Fa + 1)) & 0x10;
            break;
        default:
            throw "AF: unsupported cc_op=" + Da;
        }
        return Nc;
    }
    function fc() {
        return (Ub(2) << 0) | (Ub(10) << 2) | (Ub(4) << 6) | (Ub(8) << 7) | (Ub(0) << 11) | Pc();
    }
    function Qc() {
        var Rc;
        Rc = fc();
        Rc |= za.df & 0x00000400;
        Rc |= za.eflags;
        return Rc;
    }
    function Sc(Rc, Tc) {
        Da = 24;
        Ba = Rc & (0x0800 | 0x0080 | 0x0040 | 0x0010 | 0x0004 | 0x0001);
        za.df = 1 - (2 * ((Rc >> 10) & 1));
        za.eflags = (za.eflags & ~Tc) | (Rc & Tc);
    }
    function Uc() {
        return za.cycle_count + (xa - Na);
    }
    function Vc(qa) {
        throw "CPU abort: " + qa;
    }
    function Wc() {
        za.eip = Eb;
        za.cc_src = Ba;
        za.cc_dst = Ca;
        za.cc_op = Da;
        za.cc_op2 = Ea;
        za.cc_dst2 = Fa;
        za.dump();
    }
    function Xc(intno, error_code) {
        za.cycle_count += (xa - Na);
        za.eip = Eb;
        za.cc_src = Ba;
        za.cc_dst = Ca;
        za.cc_op = Da;
        za.cc_op2 = Ea;
        za.cc_dst2 = Fa;
        throw {
            intno: intno,
            error_code: error_code
        };
    }
    function sc(intno) {
        Xc(intno, 0);
    }
    function Yc(Zc) {
        za.cpl = Zc;
        if (za.cpl == 3) {
            Wa = Ua;
            Xa = Va;
        } else {
            Wa = Sa;
            Xa = Ta;
        }
    }
    function ad(ia, bd) {
        var Za;
        if (bd) {
            Za = Xa[ia >>> 12];
        } else {
            Za = Wa[ia >>> 12];
        }
        if (Za == -1) {
            ab(ia, bd, za.cpl == 3);
            if (bd) {
                Za = Xa[ia >>> 12];
            } else {
                Za = Wa[ia >>> 12];
            }
        }
        return Za ^ ia;
    }
    function cd() {
        var dd, l, ed, fd, i, gd;
        dd = Aa[1] >>> 0;
        l = (4096 - (Aa[6] & 0xfff)) >> 2;
        if (dd > l) dd = l;
        l = (4096 - (Aa[7] & 0xfff)) >> 2;
        if (dd > l) dd = l;
        if (dd) {
            ed = ad(Aa[6], 0);
            fd = ad(Aa[7], 1);
            gd = dd << 2;
            for (i = 0; i < gd; i++) Qa[fd + i] = Qa[ed + i];
            Aa[6] = (Aa[6] + gd) & -1;
            Aa[7] = (Aa[7] + gd) & -1;
            Aa[1] = (Aa[1] - dd) & -1;
            return true;
        }
        return false;
    }
    function hd() {
        var dd, l, fd, i, gd, ja;
        dd = Aa[1] >>> 0;
        l = (4096 - (Aa[7] & 0xfff)) >> 2;
        if (dd > l) dd = l;
        if (dd) {
            fd = ad(Aa[7], 1);
            ja = Aa[0];
            for (i = 0; i < dd; i++) {
                Qa[fd] = ja & 0xff;
                Qa[fd + 1] = (ja >> 8) & 0xff;
                Qa[fd + 2] = (ja >> 16) & 0xff;
                Qa[fd + 3] = (ja >> 24) & 0xff;
                fd += 4;
            }
            gd = dd << 2;
            Aa[7] = (Aa[7] + gd) & -1;
            Aa[1] = (Aa[1] - dd) & -1;
            return true;
        }
        return false;
    }
    function id(Eb, b) {
        var n, Ga, l, Ha, jd, base, Ma;
        n = 1;
        Ga = 0;
        kd: for (;;) {
            switch (b) {
            case 0x66:
                Ga |= 0x0100;
            case 0xf0:
            case 0xf2:
            case 0xf3:
            case 0x64:
            case 0x65:
                {
                    if ((n + 1) > 15) sc(6);
                    ia = (Eb + (n++)) >> 0;
                    b = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                };
                break;
            case 0x91:
            case 0x92:
            case 0x93:
            case 0x94:
            case 0x95:
            case 0x96:
            case 0x97:
            case 0x40:
            case 0x41:
            case 0x42:
            case 0x43:
            case 0x44:
            case 0x45:
            case 0x46:
            case 0x47:
            case 0x48:
            case 0x49:
            case 0x4a:
            case 0x4b:
            case 0x4c:
            case 0x4d:
            case 0x4e:
            case 0x4f:
            case 0x50:
            case 0x51:
            case 0x52:
            case 0x53:
            case 0x54:
            case 0x55:
            case 0x56:
            case 0x57:
            case 0x58:
            case 0x59:
            case 0x5a:
            case 0x5b:
            case 0x5c:
            case 0x5d:
            case 0x5e:
            case 0x5f:
            case 0x98:
            case 0x99:
            case 0xc9:
            case 0x9c:
            case 0x9d:
            case 0x06:
            case 0x0e:
            case 0x16:
            case 0x1e:
            case 0x07:
            case 0x17:
            case 0x1f:
            case 0xc3:
            case 0x90:
            case 0xcc:
            case 0xce:
            case 0xcf:
            case 0xf5:
            case 0xf8:
            case 0xf9:
            case 0xfc:
            case 0xfd:
            case 0xfa:
            case 0xfb:
            case 0x9e:
            case 0x9f:
            case 0xf4:
            case 0xa4:
            case 0xa5:
            case 0xaa:
            case 0xab:
            case 0xa6:
            case 0xa7:
            case 0xac:
            case 0xad:
            case 0xae:
            case 0xaf:
            case 0x9b:
            case 0xec:
            case 0xed:
            case 0xee:
            case 0xef:
            case 0xd7:
            case 0x27:
            case 0x2f:
            case 0x37:
            case 0x3f:
            case 0x60:
            case 0x61:
                break kd;
            case 0xb0:
            case 0xb1:
            case 0xb2:
            case 0xb3:
            case 0xb4:
            case 0xb5:
            case 0xb6:
            case 0xb7:
            case 0x04:
            case 0x0c:
            case 0x14:
            case 0x1c:
            case 0x24:
            case 0x2c:
            case 0x34:
            case 0x3c:
            case 0xa8:
            case 0x6a:
            case 0xeb:
            case 0x70:
            case 0x71:
            case 0x72:
            case 0x73:
            case 0x76:
            case 0x77:
            case 0x78:
            case 0x79:
            case 0x7a:
            case 0x7b:
            case 0x7c:
            case 0x7d:
            case 0x7e:
            case 0x7f:
            case 0x74:
            case 0x75:
            case 0xe2:
            case 0xe3:
            case 0xcd:
            case 0xe4:
            case 0xe5:
            case 0xe6:
            case 0xe7:
            case 0xd4:
            case 0xd5:
                n++;
                if (n > 15) sc(6);
                break kd;
            case 0xb8:
            case 0xb9:
            case 0xba:
            case 0xbb:
            case 0xbc:
            case 0xbd:
            case 0xbe:
            case 0xbf:
            case 0x05:
            case 0x0d:
            case 0x15:
            case 0x1d:
            case 0x25:
            case 0x2d:
            case 0x35:
            case 0x3d:
            case 0xa9:
            case 0x68:
            case 0xe9:
            case 0xe8:
                if (Ga & 0x0100) l = 2;
                else l = 4;
                n += l;
                if (n > 15) sc(6);
                break kd;
            case 0x88:
            case 0x89:
            case 0x8a:
            case 0x8b:
            case 0x86:
            case 0x87:
            case 0x8e:
            case 0x8c:
            case 0xc4:
            case 0xc5:
            case 0x00:
            case 0x08:
            case 0x10:
            case 0x18:
            case 0x20:
            case 0x28:
            case 0x30:
            case 0x38:
            case 0x01:
            case 0x09:
            case 0x11:
            case 0x19:
            case 0x21:
            case 0x29:
            case 0x31:
            case 0x39:
            case 0x02:
            case 0x0a:
            case 0x12:
            case 0x1a:
            case 0x22:
            case 0x2a:
            case 0x32:
            case 0x3a:
            case 0x03:
            case 0x0b:
            case 0x13:
            case 0x1b:
            case 0x23:
            case 0x2b:
            case 0x33:
            case 0x3b:
            case 0x84:
            case 0x85:
            case 0xd0:
            case 0xd1:
            case 0xd2:
            case 0xd3:
            case 0x8f:
            case 0x8d:
            case 0xfe:
            case 0xff:
            case 0xd8:
            case 0xd9:
            case 0xda:
            case 0xdb:
            case 0xdc:
            case 0xdd:
            case 0xde:
            case 0xdf:
            case 0x62:
                {
                    {
                        if ((n + 1) > 15) sc(6);
                        ia = (Eb + (n++)) >> 0;
                        Ha = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                    };
                    switch ((Ha & 7) | ((Ha >> 3) & 0x18)) {
                    case 0x04:
                        {
                            if ((n + 1) > 15) sc(6);
                            ia = (Eb + (n++)) >> 0;
                            jd = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                        };
                        if ((jd & 7) == 5) {
                            n += 4;
                            if (n > 15) sc(6);
                        }
                        break;
                    case 0x0c:
                        n += 2;
                        if (n > 15) sc(6);
                        break;
                    case 0x14:
                        n += 5;
                        if (n > 15) sc(6);
                        break;
                    case 0x05:
                        n += 4;
                        if (n > 15) sc(6);
                        break;
                    case 0x00:
                    case 0x01:
                    case 0x02:
                    case 0x03:
                    case 0x06:
                    case 0x07:
                        break;
                    case 0x08:
                    case 0x09:
                    case 0x0a:
                    case 0x0b:
                    case 0x0d:
                    case 0x0e:
                    case 0x0f:
                        n++;
                        if (n > 15) sc(6);
                        break;
                    case 0x10:
                    case 0x11:
                    case 0x12:
                    case 0x13:
                    case 0x15:
                    case 0x16:
                    case 0x17:
                        n += 4;
                        if (n > 15) sc(6);
                        break;
                    }
                };
                break kd;
            case 0xa0:
            case 0xa1:
            case 0xa2:
            case 0xa3:
                n += 4;
                if (n > 15) sc(6);
                break kd;
            case 0xc6:
            case 0x80:
            case 0x83:
            case 0x6b:
            case 0xc0:
            case 0xc1:
                {
                    {
                        if ((n + 1) > 15) sc(6);
                        ia = (Eb + (n++)) >> 0;
                        Ha = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                    };
                    switch ((Ha & 7) | ((Ha >> 3) & 0x18)) {
                    case 0x04:
                        {
                            if ((n + 1) > 15) sc(6);
                            ia = (Eb + (n++)) >> 0;
                            jd = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                        };
                        if ((jd & 7) == 5) {
                            n += 4;
                            if (n > 15) sc(6);
                        }
                        break;
                    case 0x0c:
                        n += 2;
                        if (n > 15) sc(6);
                        break;
                    case 0x14:
                        n += 5;
                        if (n > 15) sc(6);
                        break;
                    case 0x05:
                        n += 4;
                        if (n > 15) sc(6);
                        break;
                    case 0x00:
                    case 0x01:
                    case 0x02:
                    case 0x03:
                    case 0x06:
                    case 0x07:
                        break;
                    case 0x08:
                    case 0x09:
                    case 0x0a:
                    case 0x0b:
                    case 0x0d:
                    case 0x0e:
                    case 0x0f:
                        n++;
                        if (n > 15) sc(6);
                        break;
                    case 0x10:
                    case 0x11:
                    case 0x12:
                    case 0x13:
                    case 0x15:
                    case 0x16:
                    case 0x17:
                        n += 4;
                        if (n > 15) sc(6);
                        break;
                    }
                };
                n++;
                if (n > 15) sc(6);
                break kd;
            case 0xc7:
            case 0x81:
            case 0x69:
                {
                    {
                        if ((n + 1) > 15) sc(6);
                        ia = (Eb + (n++)) >> 0;
                        Ha = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                    };
                    switch ((Ha & 7) | ((Ha >> 3) & 0x18)) {
                    case 0x04:
                        {
                            if ((n + 1) > 15) sc(6);
                            ia = (Eb + (n++)) >> 0;
                            jd = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                        };
                        if ((jd & 7) == 5) {
                            n += 4;
                            if (n > 15) sc(6);
                        }
                        break;
                    case 0x0c:
                        n += 2;
                        if (n > 15) sc(6);
                        break;
                    case 0x14:
                        n += 5;
                        if (n > 15) sc(6);
                        break;
                    case 0x05:
                        n += 4;
                        if (n > 15) sc(6);
                        break;
                    case 0x00:
                    case 0x01:
                    case 0x02:
                    case 0x03:
                    case 0x06:
                    case 0x07:
                        break;
                    case 0x08:
                    case 0x09:
                    case 0x0a:
                    case 0x0b:
                    case 0x0d:
                    case 0x0e:
                    case 0x0f:
                        n++;
                        if (n > 15) sc(6);
                        break;
                    case 0x10:
                    case 0x11:
                    case 0x12:
                    case 0x13:
                    case 0x15:
                    case 0x16:
                    case 0x17:
                        n += 4;
                        if (n > 15) sc(6);
                        break;
                    }
                };
                if (Ga & 0x0100) l = 2;
                else l = 4;
                n += l;
                if (n > 15) sc(6);
                break kd;
            case 0xf6:
                {
                    {
                        if ((n + 1) > 15) sc(6);
                        ia = (Eb + (n++)) >> 0;
                        Ha = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                    };
                    switch ((Ha & 7) | ((Ha >> 3) & 0x18)) {
                    case 0x04:
                        {
                            if ((n + 1) > 15) sc(6);
                            ia = (Eb + (n++)) >> 0;
                            jd = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                        };
                        if ((jd & 7) == 5) {
                            n += 4;
                            if (n > 15) sc(6);
                        }
                        break;
                    case 0x0c:
                        n += 2;
                        if (n > 15) sc(6);
                        break;
                    case 0x14:
                        n += 5;
                        if (n > 15) sc(6);
                        break;
                    case 0x05:
                        n += 4;
                        if (n > 15) sc(6);
                        break;
                    case 0x00:
                    case 0x01:
                    case 0x02:
                    case 0x03:
                    case 0x06:
                    case 0x07:
                        break;
                    case 0x08:
                    case 0x09:
                    case 0x0a:
                    case 0x0b:
                    case 0x0d:
                    case 0x0e:
                    case 0x0f:
                        n++;
                        if (n > 15) sc(6);
                        break;
                    case 0x10:
                    case 0x11:
                    case 0x12:
                    case 0x13:
                    case 0x15:
                    case 0x16:
                    case 0x17:
                        n += 4;
                        if (n > 15) sc(6);
                        break;
                    }
                };
                Ma = (Ha >> 3) & 7;
                if (Ma == 0) {
                    n++;
                    if (n > 15) sc(6);
                }
                break kd;
            case 0xf7:
                {
                    {
                        if ((n + 1) > 15) sc(6);
                        ia = (Eb + (n++)) >> 0;
                        Ha = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                    };
                    switch ((Ha & 7) | ((Ha >> 3) & 0x18)) {
                    case 0x04:
                        {
                            if ((n + 1) > 15) sc(6);
                            ia = (Eb + (n++)) >> 0;
                            jd = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                        };
                        if ((jd & 7) == 5) {
                            n += 4;
                            if (n > 15) sc(6);
                        }
                        break;
                    case 0x0c:
                        n += 2;
                        if (n > 15) sc(6);
                        break;
                    case 0x14:
                        n += 5;
                        if (n > 15) sc(6);
                        break;
                    case 0x05:
                        n += 4;
                        if (n > 15) sc(6);
                        break;
                    case 0x00:
                    case 0x01:
                    case 0x02:
                    case 0x03:
                    case 0x06:
                    case 0x07:
                        break;
                    case 0x08:
                    case 0x09:
                    case 0x0a:
                    case 0x0b:
                    case 0x0d:
                    case 0x0e:
                    case 0x0f:
                        n++;
                        if (n > 15) sc(6);
                        break;
                    case 0x10:
                    case 0x11:
                    case 0x12:
                    case 0x13:
                    case 0x15:
                    case 0x16:
                    case 0x17:
                        n += 4;
                        if (n > 15) sc(6);
                        break;
                    }
                };
                Ma = (Ha >> 3) & 7;
                if (Ma == 0) {
                    if (Ga & 0x0100) l = 2;
                    else l = 4;
                    n += l;
                    if (n > 15) sc(6);
                }
                break kd;
            case 0xea:
                n += 6;
                if (n > 15) sc(6);
                break kd;
            case 0xc2:
                n += 2;
                if (n > 15) sc(6);
                break kd;
            case 0x26:
            case 0x2e:
            case 0x36:
            case 0x3e:
            case 0x63:
            case 0x67:
            case 0x6c:
            case 0x6d:
            case 0x6e:
            case 0x6f:
            case 0x82:
            case 0x9a:
            case 0xc8:
            case 0xca:
            case 0xcb:
            case 0xd6:
            case 0xe0:
            case 0xe1:
            case 0xf1:
            default:
                sc(6);
            case 0x0f:
                {
                    if ((n + 1) > 15) sc(6);
                    ia = (Eb + (n++)) >> 0;
                    b = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                };
                switch (b) {
                case 0x06:
                case 0xa2:
                case 0x31:
                case 0xa0:
                case 0xa8:
                case 0xa1:
                case 0xa9:
                case 0xc8:
                case 0xc9:
                case 0xca:
                case 0xcb:
                case 0xcc:
                case 0xcd:
                case 0xce:
                case 0xcf:
                    break kd;
                case 0x80:
                case 0x81:
                case 0x82:
                case 0x83:
                case 0x84:
                case 0x85:
                case 0x86:
                case 0x87:
                case 0x88:
                case 0x89:
                case 0x8a:
                case 0x8b:
                case 0x8c:
                case 0x8d:
                case 0x8e:
                case 0x8f:
                    n += 4;
                    if (n > 15) sc(6);
                    break kd;
                case 0x90:
                case 0x91:
                case 0x92:
                case 0x93:
                case 0x94:
                case 0x95:
                case 0x96:
                case 0x97:
                case 0x98:
                case 0x99:
                case 0x9a:
                case 0x9b:
                case 0x9c:
                case 0x9d:
                case 0x9e:
                case 0x9f:
                case 0x40:
                case 0x41:
                case 0x42:
                case 0x43:
                case 0x44:
                case 0x45:
                case 0x46:
                case 0x47:
                case 0x48:
                case 0x49:
                case 0x4a:
                case 0x4b:
                case 0x4c:
                case 0x4d:
                case 0x4e:
                case 0x4f:
                case 0xb6:
                case 0xb7:
                case 0xbe:
                case 0xbf:
                case 0x00:
                case 0x01:
                case 0x20:
                case 0x22:
                case 0x23:
                case 0xb2:
                case 0xb4:
                case 0xb5:
                case 0xa5:
                case 0xad:
                case 0xa3:
                case 0xab:
                case 0xb3:
                case 0xbb:
                case 0xbc:
                case 0xbd:
                case 0xaf:
                case 0xc0:
                case 0xc1:
                case 0xb1:
                    {
                        {
                            if ((n + 1) > 15) sc(6);
                            ia = (Eb + (n++)) >> 0;
                            Ha = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                        };
                        switch ((Ha & 7) | ((Ha >> 3) & 0x18)) {
                        case 0x04:
                            {
                                if ((n + 1) > 15) sc(6);
                                ia = (Eb + (n++)) >> 0;
                                jd = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                            };
                            if ((jd & 7) == 5) {
                                n += 4;
                                if (n > 15) sc(6);
                            }
                            break;
                        case 0x0c:
                            n += 2;
                            if (n > 15) sc(6);
                            break;
                        case 0x14:
                            n += 5;
                            if (n > 15) sc(6);
                            break;
                        case 0x05:
                            n += 4;
                            if (n > 15) sc(6);
                            break;
                        case 0x00:
                        case 0x01:
                        case 0x02:
                        case 0x03:
                        case 0x06:
                        case 0x07:
                            break;
                        case 0x08:
                        case 0x09:
                        case 0x0a:
                        case 0x0b:
                        case 0x0d:
                        case 0x0e:
                        case 0x0f:
                            n++;
                            if (n > 15) sc(6);
                            break;
                        case 0x10:
                        case 0x11:
                        case 0x12:
                        case 0x13:
                        case 0x15:
                        case 0x16:
                        case 0x17:
                            n += 4;
                            if (n > 15) sc(6);
                            break;
                        }
                    };
                    break kd;
                case 0xa4:
                case 0xac:
                case 0xba:
                    {
                        {
                            if ((n + 1) > 15) sc(6);
                            ia = (Eb + (n++)) >> 0;
                            Ha = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                        };
                        switch ((Ha & 7) | ((Ha >> 3) & 0x18)) {
                        case 0x04:
                            {
                                if ((n + 1) > 15) sc(6);
                                ia = (Eb + (n++)) >> 0;
                                jd = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                            };
                            if ((jd & 7) == 5) {
                                n += 4;
                                if (n > 15) sc(6);
                            }
                            break;
                        case 0x0c:
                            n += 2;
                            if (n > 15) sc(6);
                            break;
                        case 0x14:
                            n += 5;
                            if (n > 15) sc(6);
                            break;
                        case 0x05:
                            n += 4;
                            if (n > 15) sc(6);
                            break;
                        case 0x00:
                        case 0x01:
                        case 0x02:
                        case 0x03:
                        case 0x06:
                        case 0x07:
                            break;
                        case 0x08:
                        case 0x09:
                        case 0x0a:
                        case 0x0b:
                        case 0x0d:
                        case 0x0e:
                        case 0x0f:
                            n++;
                            if (n > 15) sc(6);
                            break;
                        case 0x10:
                        case 0x11:
                        case 0x12:
                        case 0x13:
                        case 0x15:
                        case 0x16:
                        case 0x17:
                            n += 4;
                            if (n > 15) sc(6);
                            break;
                        }
                    };
                    n++;
                    if (n > 15) sc(6);
                    break kd;
                case 0x02:
                case 0x03:
                case 0x04:
                case 0x05:
                case 0x07:
                case 0x08:
                case 0x09:
                case 0x0a:
                case 0x0b:
                case 0x0c:
                case 0x0d:
                case 0x0e:
                case 0x0f:
                case 0x10:
                case 0x11:
                case 0x12:
                case 0x13:
                case 0x14:
                case 0x15:
                case 0x16:
                case 0x17:
                case 0x18:
                case 0x19:
                case 0x1a:
                case 0x1b:
                case 0x1c:
                case 0x1d:
                case 0x1e:
                case 0x1f:
                case 0x21:
                case 0x24:
                case 0x25:
                case 0x26:
                case 0x27:
                case 0x28:
                case 0x29:
                case 0x2a:
                case 0x2b:
                case 0x2c:
                case 0x2d:
                case 0x2e:
                case 0x2f:
                case 0x30:
                case 0x32:
                case 0x33:
                case 0x34:
                case 0x35:
                case 0x36:
                case 0x37:
                case 0x38:
                case 0x39:
                case 0x3a:
                case 0x3b:
                case 0x3c:
                case 0x3d:
                case 0x3e:
                case 0x3f:
                case 0x50:
                case 0x51:
                case 0x52:
                case 0x53:
                case 0x54:
                case 0x55:
                case 0x56:
                case 0x57:
                case 0x58:
                case 0x59:
                case 0x5a:
                case 0x5b:
                case 0x5c:
                case 0x5d:
                case 0x5e:
                case 0x5f:
                case 0x60:
                case 0x61:
                case 0x62:
                case 0x63:
                case 0x64:
                case 0x65:
                case 0x66:
                case 0x67:
                case 0x68:
                case 0x69:
                case 0x6a:
                case 0x6b:
                case 0x6c:
                case 0x6d:
                case 0x6e:
                case 0x6f:
                case 0x70:
                case 0x71:
                case 0x72:
                case 0x73:
                case 0x74:
                case 0x75:
                case 0x76:
                case 0x77:
                case 0x78:
                case 0x79:
                case 0x7a:
                case 0x7b:
                case 0x7c:
                case 0x7d:
                case 0x7e:
                case 0x7f:
                case 0xa6:
                case 0xa7:
                case 0xaa:
                case 0xae:
                case 0xb0:
                case 0xb8:
                case 0xb9:
                case 0xc2:
                case 0xc3:
                case 0xc4:
                case 0xc5:
                case 0xc6:
                case 0xc7:
                default:
                    sc(6);
                }
                break;
            }
        }
        return n;
    }
    function ab(ld, md, ma) {
        var nd, od, error_code, pd, qd, rd, sd, bd, td;
        if (!(za.cr0 & (1 << 31))) {
            za.tlb_set_page(ld & -4096, ld & -4096, 1);
        } else {
            nd = (za.cr3 & -4096) + ((ld >> 20) & 0xffc);
            od = za.ld32_phys(nd);
            if (!(od & 0x00000001)) {
                error_code = 0;
            } else {
                if (!(od & 0x00000020)) {
                    od |= 0x00000020;
                    za.st32_phys(nd, od);
                }
                pd = (od & -4096) + ((ld >> 10) & 0xffc);
                qd = za.ld32_phys(pd);
                if (!(qd & 0x00000001)) {
                    error_code = 0;
                } else {
                    rd = qd & od;
                    if (ma && !(rd & 0x00000004)) {
                        error_code = 0x01;
                    } else if (md && !(rd & 0x00000002)) {
                        error_code = 0x01;
                    } else {
                        sd = (md && !(qd & 0x00000040));
                        if (!(qd & 0x00000020) || sd) {
                            qd |= 0x00000020;
                            if (sd) qd |= 0x00000040;
                            za.st32_phys(pd, qd);
                        }
                        bd = 0;
                        if ((qd & 0x00000040) && (rd & 0x00000002)) bd = 1;
                        td = 0;
                        if (rd & 0x00000004) td = 1;
                        za.tlb_set_page(ld & -4096, qd & -4096, bd, td);
                        return;
                    }
                }
            }
            error_code |= md << 1;
            if (ma) error_code |= 0x04;
            za.cr2 = ld;
            Xc(14, error_code);
        }
    }
    function ud(vd) {
        if (!(vd & (1 << 0))) Vc("real mode not supported");
        if ((vd & ((1 << 31) | (1 << 16) | (1 << 0))) != (za.cr0 & ((1 << 31) | (1 << 16) | (1 << 0)))) {
            za.tlb_flush_all();
        }
        za.cr0 = vd | (1 << 4);
    }
    function wd(xd) {
        za.cr3 = xd;
        if (za.cr0 & (1 << 31)) {
            za.tlb_flush_all();
        }
    }
    function yd(zd) {
        za.cr4 = zd;
    }
    function Ad(Bd) {
        if (Bd & (1 << 22)) return -1;
        else return 0xffff;
    }
    function Cd(selector) {
        var va, Mb, Dd, Bd;
        if (selector & 0x4) va = za.ldt;
        else va = za.gdt;
        Mb = selector & ~7;
        if ((Mb + 7) > va.limit) return null;
        ia = va.base + Mb;
        Dd = xb();
        ia += 4;
        Bd = xb();
        return [Dd, Bd];
    }
    function Ed(Dd, Bd) {
        var limit;
        limit = (Dd & 0xffff) | (Bd & 0x000f0000);
        if (Bd & (1 << 23)) limit = (limit << 12) | 0xfff;
        return limit;
    }
    function Fd(Dd, Bd) {
        return (((Dd >>> 16) | ((Bd & 0xff) << 16) | (Bd & 0xff000000))) & -1;
    }
    function Gd(va, Dd, Bd) {
        va.base = Fd(Dd, Bd);
        va.limit = Ed(Dd, Bd);
        va.flags = Bd;
    }
    function Hd(Id, selector, base, limit, flags) {
        za.segs[Id] = {
            selector: selector,
            base: base,
            limit: limit,
            flags: flags
        };
    }
    function Jd(Kd) {
        var Ld, Mb, Md, Nd, Od;
        if (!(za.tr.flags & (1 << 15))) Vc("invalid tss");
        Ld = (za.tr.flags >> 8) & 0xf;
        if ((Ld & 7) != 1) Vc("invalid tss type");
        Md = Ld >> 3;
        Mb = (Kd * 4 + 2) << Md;
        if (Mb + (4 << Md) - 1 > za.tr.limit) Xc(10, za.tr.selector & 0xfffc);
        ia = (za.tr.base + Mb) & -1;
        if (Md == 0) {
            Od = vb();
            ia += 2;
        } else {
            Od = xb();
            ia += 4;
        }
        Nd = vb();
        return [Nd, Od];
    }
    function Pd(intno, Qd, error_code, Rd, Sd) {
        var va, Td, Ld, Kd, selector, Ud, Vd;
        var Wd, Xd, Md;
        var e, Dd, Bd, Yd, Nd, Od, Zd, ae;
        var be, ce;
        if (intno == 0x06) {
            var de = Eb;
            qa = "do_interrupt: intno=" + ta(intno) + " error_code=" + sa(error_code) + " EIP=" + sa(de) + " ESP=" + sa(Aa[4]) + " EAX=" + sa(Aa[0]) + " EBX=" + sa(Aa[3]) + " ECX=" + sa(Aa[1]);
            if (intno == 0x0e) {
                qa += " CR2=" + sa(za.cr2);
            }
            console.log(qa);
            if (intno == 0x06) {
                var qa, i, n;
                qa = "Code:";
                n = 4096 - (de & 0xfff);
                if (n > 15) n = 15;
                for (i = 0; i < n; i++) {
                    ia = (de + i) & -1;
                    qa += " " + ta(bb());
                }
                console.log(qa);
            }
        }
        Wd = 0;
        if (!Qd && !Sd) {
            switch (intno) {
            case 8:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 17:
                Wd = 1;
                break;
            }
        }
        if (Qd) be = Rd;
        else be = Eb;
        va = za.idt;
        if (intno * 8 + 7 > va.limit) Xc(13, intno * 8 + 2);
        ia = (va.base + intno * 8) & -1;
        Dd = xb();
        ia += 4;
        Bd = xb();
        Ld = (Bd >> 8) & 0x1f;
        switch (Ld) {
        case 5:
        case 7:
        case 6:
            throw "unsupported task gate";
        case 14:
        case 15:
            break;
        default:
            Xc(13, intno * 8 + 2);
            break;
        }
        Kd = (Bd >> 13) & 3;
        Vd = za.cpl;
        if (Qd && Kd < Vd) Xc(13, intno * 8 + 2);
        if (!(Bd & (1 << 15))) Xc(11, intno * 8 + 2);
        selector = Dd >> 16;
        Yd = (Bd & -65536) | (Dd & 0x0000ffff);
        if ((selector & 0xfffc) == 0) Xc(13, 0);
        e = Cd(selector);
        if (!e) Xc(13, selector & 0xfffc);
        Dd = e[0];
        Bd = e[1];
        if (!(Bd & (1 << 12)) || !(Bd & ((1 << 11)))) Xc(13, selector & 0xfffc);
        Kd = (Bd >> 13) & 3;
        if (Kd > Vd) Xc(13, selector & 0xfffc);
        if (!(Bd & (1 << 15))) Xc(11, selector & 0xfffc);
        if (!(Bd & (1 << 10)) && Kd < Vd) {
            e = Jd(Kd);
            Nd = e[0];
            Od = e[1];
            if ((Nd & 0xfffc) == 0) Xc(10, Nd & 0xfffc);
            if ((Nd & 3) != Kd) Xc(10, Nd & 0xfffc);
            e = Cd(Nd);
            if (!e) Xc(10, Nd & 0xfffc);
            Zd = e[0];
            ae = e[1];
            Ud = (ae >> 13) & 3;
            if (Ud != Kd) Xc(10, Nd & 0xfffc);
            if (!(ae & (1 << 12)) || (ae & (1 << 11)) || !(ae & (1 << 9))) Xc(10, Nd & 0xfffc);
            if (!(ae & (1 << 15))) Xc(10, Nd & 0xfffc);
            Xd = 1;
            ce = Ad(ae);
            Td = Fd(Zd, ae);
        } else if ((Bd & (1 << 10)) || Kd == Vd) {
            if (za.eflags & 0x00020000) Xc(13, selector & 0xfffc);
            Xd = 0;
            ce = Ad(za.segs[2].flags);
            Td = za.segs[2].base;
            Od = Aa[4];
            Kd = Vd;
        } else {
            Xc(13, selector & 0xfffc);
            Xd = 0;
            ce = 0;
            Td = 0;
            Od = 0;
        }
        Md = Ld >> 3;
        if (Xd) {
            if (za.eflags & 0x00020000) {
                {
                    Od = (Od - 4) & -1;
                    ia = (Td + (Od & ce)) & -1;
                    Db(za.segs[5].selector);
                }; {
                    Od = (Od - 4) & -1;
                    ia = (Td + (Od & ce)) & -1;
                    Db(za.segs[4].selector);
                }; {
                    Od = (Od - 4) & -1;
                    ia = (Td + (Od & ce)) & -1;
                    Db(za.segs[3].selector);
                }; {
                    Od = (Od - 4) & -1;
                    ia = (Td + (Od & ce)) & -1;
                    Db(za.segs[0].selector);
                };
            } {
                Od = (Od - 4) & -1;
                ia = (Td + (Od & ce)) & -1;
                Db(za.segs[2].selector);
            }; {
                Od = (Od - 4) & -1;
                ia = (Td + (Od & ce)) & -1;
                Db(Aa[4]);
            };
        } {
            Od = (Od - 4) & -1;
            ia = (Td + (Od & ce)) & -1;
            Db(Qc());
        }; {
            Od = (Od - 4) & -1;
            ia = (Td + (Od & ce)) & -1;
            Db(za.segs[1].selector);
        }; {
            Od = (Od - 4) & -1;
            ia = (Td + (Od & ce)) & -1;
            Db(be);
        };
        if (Wd) {
            {
                Od = (Od - 4) & -1;
                ia = (Td + (Od & ce)) & -1;
                Db(error_code);
            };
        }
        if (Xd) {
            if (za.eflags & 0x00020000) {
                Hd(0, 0, 0, 0, 0);
                Hd(3, 0, 0, 0, 0);
                Hd(4, 0, 0, 0, 0);
                Hd(5, 0, 0, 0, 0);
            }
            Nd = (Nd & ~3) | Kd;
            Hd(2, Nd, Td, Ed(Zd, ae), ae);
        }
        Aa[4] = (Aa[4] & ~ (ce)) | ((Od) & (ce));
        selector = (selector & ~3) | Kd;
        Hd(1, selector, Fd(Dd, Bd), Ed(Dd, Bd), Bd);
        Yc(Kd);
        Eb = Yd, Fb = Hb = 0;
        if ((Ld & 1) == 0) {
            za.eflags &= ~0x00000200;
        }
        za.eflags &= ~ (0x00000100 | 0x00020000 | 0x00010000 | 0x00004000);
    }
    function ee(selector) {
        var va, Dd, Bd, Mb, fe;
        selector &= 0xffff;
        if ((selector & 0xfffc) == 0) {
            za.ldt.base = 0;
            za.ldt.limit = 0;
        } else {
            if (selector & 0x4) Xc(13, selector & 0xfffc);
            va = za.gdt;
            Mb = selector & ~7;
            fe = 7;
            if ((Mb + fe) > va.limit) Xc(13, selector & 0xfffc);
            ia = (va.base + Mb) & -1;
            Dd = xb();
            ia += 4;
            Bd = xb();
            if ((Bd & (1 << 12)) || ((Bd >> 8) & 0xf) != 2) Xc(13, selector & 0xfffc);
            if (!(Bd & (1 << 15))) Xc(11, selector & 0xfffc);
            Gd(za.ldt, Dd, Bd);
        }
        za.ldt.selector = selector;
    }
    function ge(selector) {
        var va, Dd, Bd, Mb, Ld, fe;
        selector &= 0xffff;
        if ((selector & 0xfffc) == 0) {
            za.tr.base = 0;
            za.tr.limit = 0;
            za.tr.flags = 0;
        } else {
            if (selector & 0x4) Xc(13, selector & 0xfffc);
            va = za.gdt;
            Mb = selector & ~7;
            fe = 7;
            if ((Mb + fe) > va.limit) Xc(13, selector & 0xfffc);
            ia = (va.base + Mb) & -1;
            Dd = xb();
            ia += 4;
            Bd = xb();
            Ld = (Bd >> 8) & 0xf;
            if ((Bd & (1 << 12)) || (Ld != 1 && Ld != 9)) Xc(13, selector & 0xfffc);
            if (!(Bd & (1 << 15))) Xc(11, selector & 0xfffc);
            Gd(za.tr, Dd, Bd);
            Bd |= (1 << 9);
            Db(Bd);
        }
        za.tr.selector = selector;
    }
    function he(ie, selector) {
        var Dd, Bd, Vd, Kd, je, va, Mb;
        selector &= 0xffff;
        Vd = za.cpl;
        if ((selector & 0xfffc) == 0) {
            if (ie == 2) Xc(13, 0);
            Hd(ie, selector, 0, 0, 0);
        } else {
            if (selector & 0x4) va = za.ldt;
            else va = za.gdt;
            Mb = selector & ~7;
            if ((Mb + 7) > va.limit) Xc(13, selector & 0xfffc);
            ia = (va.base + Mb) & -1;
            Dd = xb();
            ia += 4;
            Bd = xb();
            if (!(Bd & (1 << 12))) Xc(13, selector & 0xfffc);
            je = selector & 3;
            Kd = (Bd >> 13) & 3;
            if (ie == 2) {
                if ((Bd & (1 << 11)) || !(Bd & (1 << 9))) Xc(13, selector & 0xfffc);
                if (je != Vd || Kd != Vd) Xc(13, selector & 0xfffc);
            } else {
                if ((Bd & ((1 << 11) | (1 << 9))) == (1 << 11)) Xc(13, selector & 0xfffc);
                if (!(Bd & (1 << 11)) || !(Bd & (1 << 10))) {
                    if (Kd < Vd || Kd < je) Xc(13, selector & 0xfffc);
                }
            }
            if (!(Bd & (1 << 15))) {
                if (ie == 2) Xc(12, selector & 0xfffc);
                else Xc(11, selector & 0xfffc);
            }
            if (!(Bd & (1 << 8))) {
                Bd |= (1 << 8);
                Db(Bd);
            }
            Hd(ie, selector, Fd(Dd, Bd), Ed(Dd, Bd), Bd);
        }
    }
    function ke(le, me) {
        var ne, Ld, Dd, Bd, Vd, Kd, je, limit, e;
        if ((le & 0xfffc) == 0) Xc(13, 0);
        e = Cd(le);
        if (!e) Xc(13, le & 0xfffc);
        Dd = e[0];
        Bd = e[1];
        Vd = za.cpl;
        if (Bd & (1 << 12)) {
            if (!(Bd & (1 << 11))) Xc(13, le & 0xfffc);
            Kd = (Bd >> 13) & 3;
            if (Bd & (1 << 10)) {
                if (Kd > Vd) Xc(13, le & 0xfffc);
            } else {
                je = le & 3;
                if (je > Vd) Xc(13, le & 0xfffc);
                if (Kd != Vd) Xc(13, le & 0xfffc);
            }
            if (!(Bd & (1 << 15))) Xc(11, le & 0xfffc);
            limit = Ed(Dd, Bd);
            if ((me >>> 0) > (limit >>> 0)) Xc(13, le & 0xfffc);
            Hd(1, (le & 0xfffc) | Vd, Fd(Dd, Bd), limit, Bd);
            Eb = me, Fb = Hb = 0;
        } else {
            Vc("unsupported jump to call or task gate");
        }
    }
    function oe(ie, Vd) {
        var Kd, Bd;
        if ((ie == 4 || ie == 5) && (za.segs[ie].selector & 0xfffc) == 0) return;
        Bd = za.segs[ie].flags;
        Kd = (Bd >> 13) & 3;
        if (!(Bd & (1 << 11)) || !(Bd & (1 << 10))) {
            if (Kd < Vd) {
                Hd(ie, 0, 0, 0, 0);
            }
        }
    }
    function pe(Md, qe, re) {
        var le, se, te;
        var ue, ve, we, xe;
        var e, Dd, Bd, Zd, ae;
        var Vd, Kd, je, ye, ze;
        var Td, Ae, me, Be, ce;
        ce = Ad(za.segs[2].flags);
        Ae = Aa[4];
        Td = za.segs[2].base;
        se = 0;
        if (Md == 1) {
            {
                ia = (Td + (Ae & ce)) & -1;
                me = fb();
                Ae = (Ae + 4) & -1;
            }; {
                ia = (Td + (Ae & ce)) & -1;
                le = fb();
                Ae = (Ae + 4) & -1;
            };
            le &= 0xffff;
            if (qe) {
                {
                    ia = (Td + (Ae & ce)) & -1;
                    se = fb();
                    Ae = (Ae + 4) & -1;
                };
                if (se & 0x00020000) throw "VM86 unsupported";
            }
        } else {
            throw "unsupported";
        }
        if ((le & 0xfffc) == 0) Xc(13, le & 0xfffc);
        e = Cd(le);
        if (!e) Xc(13, le & 0xfffc);
        Dd = e[0];
        Bd = e[1];
        if (!(Bd & (1 << 12)) || !(Bd & (1 << 11))) Xc(13, le & 0xfffc);
        Vd = za.cpl;
        je = le & 3;
        if (je < Vd) Xc(13, le & 0xfffc);
        Kd = (Bd >> 13) & 3;
        if (Bd & (1 << 10)) {
            if (Kd > je) Xc(13, le & 0xfffc);
        } else {
            if (Kd != je) Xc(13, le & 0xfffc);
        }
        if (!(Bd & (1 << 15))) Xc(11, le & 0xfffc);
        Ae = (Ae + re) & -1;
        if (je == Vd) {
            Hd(1, le, Fd(Dd, Bd), Ed(Dd, Bd), Bd);
        } else {
            if (Md == 1) {
                {
                    ia = (Td + (Ae & ce)) & -1;
                    Be = fb();
                    Ae = (Ae + 4) & -1;
                }; {
                    ia = (Td + (Ae & ce)) & -1;
                    te = fb();
                    Ae = (Ae + 4) & -1;
                };
                te &= 0xffff;
            } else {
                throw "unsupported";
            }
            if ((te & 0xfffc) == 0) {
                Xc(13, 0);
            } else {
                if ((te & 3) != je) Xc(13, te & 0xfffc);
                e = Cd(te);
                if (!e) Xc(13, te & 0xfffc);
                Zd = e[0];
                ae = e[1];
                if (!(ae & (1 << 12)) || (ae & (1 << 11)) || !(ae & (1 << 9))) Xc(13, te & 0xfffc);
                Kd = (ae >> 13) & 3;
                if (Kd != je) Xc(13, te & 0xfffc);
                if (!(ae & (1 << 15))) Xc(11, te & 0xfffc);
                Hd(2, te, Fd(Zd, ae), Ed(Zd, ae), ae);
            }
            Hd(1, le, Fd(Dd, Bd), Ed(Dd, Bd), Bd);
            Yc(je);
            Ae = Be;
            ce = Ad(ae);
            oe(0, je);
            oe(3, je);
            oe(4, je);
            oe(5, je);
            Ae = (Ae + re) & -1;
        }
        Aa[4] = (Aa[4] & ~ (ce)) | ((Ae) & (ce));
        Eb = me, Fb = Hb = 0;
        if (qe) {
            ye = 0x00000100 | 0x00040000 | 0x00200000 | 0x00010000 | 0x00004000;
            if (Vd == 0) ye |= 0x00003000;
            ze = (za.eflags >> 12) & 3;
            if (Vd <= ze) ye |= 0x00000200;
            if (Md == 0) ye &= 0xffff;
            Sc(se, ye);
        }
    }
    function Ce(Md) {
        if (za.eflags & 0x00004000) {
            Xc(13, 0);
        } else {
            pe(Md, 1, 0);
        }
    }
    function De() {
        var Mb;
        Mb = Aa[0];
        switch (Mb) {
        case 0:
            Aa[0] = 1;
            Aa[3] = 0x756e6547 & -1;
            Aa[2] = 0x49656e69 & -1;
            Aa[1] = 0x6c65746e & -1;
            break;
        case 1:
        default:
            Aa[0] = (5 << 8) | (4 << 4) | 3;
            Aa[3] = 8 << 8;
            Aa[1] = 0;
            Aa[2] = (1 << 4);
            break;
        }
    }
    function Ee(base) {
        var Fe, Ge;
        if (base == 0) sc(0);
        Fe = Aa[0] & 0xff;
        Ge = (Fe / base) & -1;
        Fe = (Fe % base);
        Aa[0] = (Aa[0] & ~0xffff) | Fe | (Ge << 8);
        Ca = Fe;
        Da = 12;
    }
    function He(base) {
        var Fe, Ge;
        Fe = Aa[0] & 0xff;
        Ge = (Aa[0] >> 8) & 0xff;
        Fe = (Ge * base + Fe) & 0xff;
        Aa[0] = (Aa[0] & ~0xffff) | Fe;
        Ca = Fe;
        Da = 12;
    }
    function Ie() {
        var Je, Fe, Ge, Ke, Rc;
        Rc = fc();
        Ke = Rc & 0x0010;
        Fe = Aa[0] & 0xff;
        Ge = (Aa[0] >> 8) & 0xff;
        Je = (Fe > 0xf9);
        if (((Fe & 0x0f) > 9) || Ke) {
            Fe = (Fe + 6) & 0x0f;
            Ge = (Ge + 1 + Je) & 0xff;
            Rc |= 0x0001 | 0x0010;
        } else {
            Rc &= ~ (0x0001 | 0x0010);
            Fe &= 0x0f;
        }
        Aa[0] = (Aa[0] & ~0xffff) | Fe | (Ge << 8);
        Ba = Rc;
        Da = 24;
    }
    function Le() {
        var Je, Fe, Ge, Ke, Rc;
        Rc = fc();
        Ke = Rc & 0x0010;
        Fe = Aa[0] & 0xff;
        Ge = (Aa[0] >> 8) & 0xff;
        Je = (Fe < 6);
        if (((Fe & 0x0f) > 9) || Ke) {
            Fe = (Fe - 6) & 0x0f;
            Ge = (Ge - 1 - Je) & 0xff;
            Rc |= 0x0001 | 0x0010;
        } else {
            Rc &= ~ (0x0001 | 0x0010);
            Fe &= 0x0f;
        }
        Aa[0] = (Aa[0] & ~0xffff) | Fe | (Ge << 8);
        Ba = Rc;
        Da = 24;
    }
    function Me() {
        var Fe, Ke, Ne, Rc;
        Rc = fc();
        Ne = Rc & 0x0001;
        Ke = Rc & 0x0010;
        Fe = Aa[0] & 0xff;
        Rc = 0;
        if (((Fe & 0x0f) > 9) || Ke) {
            Fe = (Fe + 6) & 0xff;
            Rc |= 0x0010;
        }
        if ((Fe > 0x9f) || Ne) {
            Fe = (Fe + 0x60) & 0xff;
            Rc |= 0x0001;
        }
        Aa[0] = (Aa[0] & ~0xff) | Fe;
        Rc |= (Fe == 0) << 6;
        Rc |= aa[Fe] << 2;
        Rc |= (Fe & 0x80);
        Ba = Rc;
        Da = 24;
    }
    function Oe() {
        var Fe, Pe, Ke, Ne, Rc;
        Rc = fc();
        Ne = Rc & 0x0001;
        Ke = Rc & 0x0010;
        Fe = Aa[0] & 0xff;
        Rc = 0;
        Pe = Fe;
        if (((Fe & 0x0f) > 9) || Ke) {
            Rc |= 0x0010;
            if (Fe < 6 || Ne) Rc |= 0x0001;
            Fe = (Fe - 6) & 0xff;
        }
        if ((Pe > 0x99) || Ne) {
            Fe = (Fe - 0x60) & 0xff;
            Rc |= 0x0001;
        }
        Aa[0] = (Aa[0] & ~0xff) | Fe;
        Rc |= (Fe == 0) << 6;
        Rc |= aa[Fe] << 2;
        Rc |= (Fe & 0x80);
        Ba = Rc;
        Da = 24;
    }
    function Qe() {
        var Ha, ja, Ka, La;
        Ha = Qa[Fb++];;
        if ((Ha >> 3) == 3) sc(6);
        ia = Jb(Ha);
        ja = fb();
        ia = (ia + 4) & -1;
        Ka = fb();
        Ja = (Ha >> 3) & 7;
        La = Aa[Ja];
        if (La < ja || La > Ka) sc(5);
    }
    function Re() {
        var Ha, ja, Ka, La;
        Ha = Qa[Fb++];;
        if ((Ha >> 3) == 3) sc(6);
        ia = Jb(Ha);
        ja = (db() << 16) >> 16;
        ia = (ia + 2) & -1;
        Ka = (db() << 16) >> 16;
        Ja = (Ha >> 3) & 7;
        La = (Aa[Ja] << 16) >> 16;
        if (La < ja || La > Ka) sc(5);
    }
    za = this;
    Qa = this.phys_mem8;
    Ua = this.tlb_read_user;
    Va = this.tlb_write_user;
    Sa = this.tlb_read_kernel;
    Ta = this.tlb_write_kernel;
    if (za.cpl == 3) {
        Wa = Ua;
        Xa = Va;
    } else {
        Wa = Sa;
        Xa = Ta;
    }
    if (za.halted) {
        if (za.hard_irq != 0 && (za.eflags & 0x00000200)) {
            za.halted = 0;
        } else {
            return 257;
        }
    }
    Aa = this.regs;
    Ba = this.cc_src;
    Ca = this.cc_dst;
    Da = this.cc_op;
    Ea = this.cc_op2;
    Fa = this.cc_dst2;
    Eb = this.eip;
    Oa = 256;
    Na = xa;
    if (ya) {;
        Pd(ya.intno, 0, ya.error_code, 0, 0);
    }
    if (za.hard_intno >= 0) {;
        Pd(za.hard_intno, 0, 0, 0, 1);
        za.hard_intno = -1;
    }
    if (za.hard_irq != 0 && (za.eflags & 0x00000200)) {
        za.hard_intno = za.get_hard_intno();;
        Pd(za.hard_intno, 0, 0, 0, 1);
        za.hard_intno = -1;
    }
    Fb = 0;
    Hb = 0;
    Se: do {;
        Ga = 0;
        Eb = (Eb + Fb - Hb) >> 0;
        Gb = Wa[Eb >>> 12];
        if (((Gb | Eb) & 0xfff) >= (4096 - 15 + 1)) {
            var Te;
            if (Gb == -1) ab(Eb, 0, za.cpl == 3);
            Gb = Wa[Eb >>> 12];
            Hb = Fb = Eb ^ Gb;
            b = Qa[Fb++];;
            Te = Eb & 0xfff;
            if (Te >= (4096 - 15 + 1)) {
                ja = id(Eb, b);
                if ((Te + ja) > 4096) {
                    Hb = Fb = this.mem_size;
                    for (Ka = 0; Ka < ja; Ka++) {
                        ia = (Eb + Ka) >> 0;
                        Qa[Fb + Ka] = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                    }
                    Fb++;
                }
            }
        } else {
            Hb = Fb = Eb ^ Gb;
            b = Qa[Fb++];;
        }
        if (0) {
            console.log("exec: EIP=" + sa(Eb) + " OPCODE=" + sa(b));
        }
        kd: for (;;) {
            switch (b) {
            case 0x66:
                if (Ga == 0) id(Eb, b);
                Ga |= 0x0100;
                b = Qa[Fb++];;
                b |= (Ga & 0x0100);
                break;
            case 0xf0:
                if (Ga == 0) id(Eb, b);
                Ga |= 0x0040;
                b = Qa[Fb++];;
                b |= (Ga & 0x0100);
                break;
            case 0xf2:
                if (Ga == 0) id(Eb, b);
                Ga |= 0x0020;
                b = Qa[Fb++];;
                b |= (Ga & 0x0100);
                break;
            case 0xf3:
                if (Ga == 0) id(Eb, b);
                Ga |= 0x0010;
                b = Qa[Fb++];;
                b |= (Ga & 0x0100);
                break;
            case 0x64:
                if (Ga == 0) id(Eb, b);
                Ga = (Ga & ~0x000f) | (4 + 1);
                b = Qa[Fb++];;
                b |= (Ga & 0x0100);;
                break;
            case 0x65:
                if (Ga == 0) id(Eb, b);
                Ga = (Ga & ~0x000f) | (5 + 1);
                b = Qa[Fb++];;
                b |= (Ga & 0x0100);;
                break;
            case 0xb0:
            case 0xb1:
            case 0xb2:
            case 0xb3:
            case 0xb4:
            case 0xb5:
            case 0xb6:
            case 0xb7:
                ja = Qa[Fb++];;
                b &= 7;
                Ra = (b & 4) << 1;
                Aa[b & 3] = (Aa[b & 3] & ~ (0xff << Ra)) | (((ja) & 0xff) << Ra);
                break kd;
            case 0xb8:
            case 0xb9:
            case 0xba:
            case 0xbb:
            case 0xbc:
            case 0xbd:
            case 0xbe:
            case 0xbf:
                {
                    ja = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                    Fb += 4;
                };
                Aa[b & 7] = ja;
                break kd;
            case 0x88:
                Ha = Qa[Fb++];;
                Ja = (Ha >> 3) & 7;
                ja = ((Aa[Ja & 3] >> ((Ja & 4) << 1)) & 0xff);
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7;
                    Ra = (Ia & 4) << 1;
                    Aa[Ia & 3] = (Aa[Ia & 3] & ~ (0xff << Ra)) | (((ja) & 0xff) << Ra);
                } else {
                    ia = Jb(Ha); {
                        Ra = Xa[ia >>> 12];
                        if (Ra == -1) {
                            mb(ja);
                        } else {
                            Qa[ia ^ Ra] = ja & 0xff;
                        }
                    };
                }
                break kd;
            case 0x89:
                Ha = Qa[Fb++];;
                ja = Aa[(Ha >> 3) & 7];
                if ((Ha >> 6) == 3) {
                    Aa[Ha & 7] = ja;
                } else {
                    ia = Jb(Ha); {
                        Ra = Xa[ia >>> 12];
                        if ((Ra | ia) & 3) {
                            qb(ja);
                        } else {
                            Ra ^= ia;
                            Qa[Ra] = ja & 0xff;
                            Qa[Ra + 1] = (ja >> 8) & 0xff;
                            Qa[Ra + 2] = (ja >> 16) & 0xff;
                            Qa[Ra + 3] = (ja >> 24) & 0xff;
                        }
                    };
                }
                break kd;
            case 0x8a:
                Ha = Qa[Fb++];;
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7;
                    ja = ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff);
                } else {
                    ia = Jb(Ha);
                    ja = (((Ra = Wa[ia >>> 12]) == -1) ? Ya() : Qa[ia ^ Ra]);
                }
                Ja = (Ha >> 3) & 7;
                Ra = (Ja & 4) << 1;
                Aa[Ja & 3] = (Aa[Ja & 3] & ~ (0xff << Ra)) | (((ja) & 0xff) << Ra);
                break kd;
            case 0x8b:
                Ha = Qa[Fb++];;
                if ((Ha >> 6) == 3) {
                    ja = Aa[Ha & 7];
                } else {
                    ia = Jb(Ha);
                    ja = (((Ra = Wa[ia >>> 12]) | ia) & 3 ? eb() : (Ra ^= ia, Qa[Ra] | (Qa[Ra + 1] << 8) | (Qa[Ra + 2] << 16) | (Qa[Ra + 3] << 24)));
                }
                Aa[(Ha >> 3) & 7] = ja;
                break kd;
            case 0xa0:
                ia = Nb();
                ja = bb();
                Aa[0] = (Aa[0] & -256) | ja;
                break kd;
            case 0xa1:
                ia = Nb();
                ja = fb();
                Aa[0] = ja;
                break kd;
            case 0xa2:
                ia = Nb();
                nb(Aa[0]);
                break kd;
            case 0xa3:
                ia = Nb();
                rb(Aa[0]);
                break kd;
            case 0xd7:
                ia = (Aa[3] + (Aa[0] & 0xff)) & -1;
                if (Ga & 0x000f) {
                    ia = (ia + za.segs[(Ga & 0x000f) - 1].base) & -1;
                }
                ja = bb();
                Ob(0, ja);
                break kd;
            case 0xc6:
                Ha = Qa[Fb++];;
                if ((Ha >> 6) == 3) {
                    ja = Qa[Fb++];;
                    Ob(Ha & 7, ja);
                } else {
                    ia = Jb(Ha);
                    ja = Qa[Fb++];;
                    nb(ja);
                }
                break kd;
            case 0xc7:
                Ha = Qa[Fb++];;
                if ((Ha >> 6) == 3) {
                    {
                        ja = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                        Fb += 4;
                    };
                    Aa[Ha & 7] = ja;
                } else {
                    ia = Jb(Ha); {
                        ja = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                        Fb += 4;
                    };
                    rb(ja);
                }
                break kd;
            case 0x91:
            case 0x92:
            case 0x93:
            case 0x94:
            case 0x95:
            case 0x96:
            case 0x97:
                Ja = b & 7;
                ja = Aa[0];
                Aa[0] = Aa[Ja];
                Aa[Ja] = ja;
                break kd;
            case 0x86:
                Ha = Qa[Fb++];;
                Ja = (Ha >> 3) & 7;
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7;
                    ja = ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff);
                    Ob(Ia, ((Aa[Ja & 3] >> ((Ja & 4) << 1)) & 0xff));
                } else {
                    ia = Jb(Ha);
                    ja = hb();
                    nb(((Aa[Ja & 3] >> ((Ja & 4) << 1)) & 0xff));
                }
                Ob(Ja, ja);
                break kd;
            case 0x87:
                Ha = Qa[Fb++];;
                Ja = (Ha >> 3) & 7;
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7;
                    ja = Aa[Ia];
                    Aa[Ia] = Aa[Ja];
                } else {
                    ia = Jb(Ha);
                    ja = lb();
                    rb(Aa[Ja]);
                }
                Aa[Ja] = ja;
                break kd;
            case 0x8e:
                Ha = Qa[Fb++];;
                Ja = (Ha >> 3) & 7;
                if (Ja >= 6 || Ja == 1) sc(6);
                if ((Ha >> 6) == 3) {
                    ja = Aa[Ha & 7] & 0xffff;
                } else {
                    ia = Jb(Ha);
                    ja = db();
                }
                he(Ja, ja);
                break kd;
            case 0x8c:
                Ha = Qa[Fb++];;
                Ja = (Ha >> 3) & 7;
                if (Ja >= 6) sc(6);
                ja = za.segs[Ja].selector;
                if ((Ha >> 6) == 3) {
                    Aa[Ha & 7] = ja;
                } else {
                    ia = Jb(Ha);
                    pb(ja);
                }
                break kd;
            case 0xc4:
                {
                    Ha = Qa[Fb++];;
                    if ((Ha >> 3) == 3) sc(6);
                    ia = Jb(Ha);
                    ja = fb();
                    ia += 4;
                    Ka = db();
                    he(0, Ka);
                    Aa[(Ha >> 3) & 7] = ja;
                };
                break kd;
            case 0xc5:
                {
                    Ha = Qa[Fb++];;
                    if ((Ha >> 3) == 3) sc(6);
                    ia = Jb(Ha);
                    ja = fb();
                    ia += 4;
                    Ka = db();
                    he(3, Ka);
                    Aa[(Ha >> 3) & 7] = ja;
                };
                break kd;
            case 0x00:
            case 0x08:
            case 0x10:
            case 0x18:
            case 0x20:
            case 0x28:
            case 0x30:
            case 0x38:
                Ha = Qa[Fb++];;
                Ma = b >> 3;
                Ja = (Ha >> 3) & 7;
                Ka = ((Aa[Ja & 3] >> ((Ja & 4) << 1)) & 0xff);
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7;
                    Ob(Ia, Qb(Ma, ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff), Ka));
                } else {
                    ia = Jb(Ha);
                    if (Ma != 7) {
                        ja = hb();
                        ja = Qb(Ma, ja, Ka);
                        nb(ja);
                    } else {
                        ja = bb();
                        Qb(7, ja, Ka);
                    }
                }
                break kd;
            case 0x01:
            case 0x09:
            case 0x11:
            case 0x19:
            case 0x21:
            case 0x29:
            case 0x31:
                Ha = Qa[Fb++];;
                Ma = b >> 3;
                Ka = Aa[(Ha >> 3) & 7];
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7;
                    Aa[Ia] = ac(Ma, Aa[Ia], Ka);
                } else {
                    ia = Jb(Ha);
                    ja = lb();
                    ja = ac(Ma, ja, Ka);
                    rb(ja);
                }
                break kd;
            case 0x39:
                Ha = Qa[Fb++];;
                Ma = b >> 3;
                Ka = Aa[(Ha >> 3) & 7];
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7; {
                        Ba = Ka;
                        Ca = (Aa[Ia] - Ba) & -1;
                        Da = 8;
                    };
                } else {
                    ia = Jb(Ha);
                    ja = fb(); {
                        Ba = Ka;
                        Ca = (ja - Ba) & -1;
                        Da = 8;
                    };
                }
                break kd;
            case 0x02:
            case 0x0a:
            case 0x12:
            case 0x1a:
            case 0x22:
            case 0x2a:
            case 0x32:
            case 0x3a:
                Ha = Qa[Fb++];;
                Ma = b >> 3;
                Ja = (Ha >> 3) & 7;
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7;
                    Ka = ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff);
                } else {
                    ia = Jb(Ha);
                    Ka = bb();
                }
                Ob(Ja, Qb(Ma, ((Aa[Ja & 3] >> ((Ja & 4) << 1)) & 0xff), Ka));
                break kd;
            case 0x03:
            case 0x0b:
            case 0x13:
            case 0x1b:
            case 0x23:
            case 0x2b:
            case 0x33:
                Ha = Qa[Fb++];;
                Ma = b >> 3;
                Ja = (Ha >> 3) & 7;
                if ((Ha >> 6) == 3) {
                    Ka = Aa[Ha & 7];
                } else {
                    ia = Jb(Ha);
                    Ka = fb();
                }
                Aa[Ja] = ac(Ma, Aa[Ja], Ka);
                break kd;
            case 0x3b:
                Ha = Qa[Fb++];;
                Ma = b >> 3;
                Ja = (Ha >> 3) & 7;
                if ((Ha >> 6) == 3) {
                    Ka = Aa[Ha & 7];
                } else {
                    ia = Jb(Ha);
                    Ka = fb();
                } {
                    Ba = Ka;
                    Ca = (Aa[Ja] - Ba) & -1;
                    Da = 8;
                };
                break kd;
            case 0x04:
            case 0x0c:
            case 0x14:
            case 0x1c:
            case 0x24:
            case 0x2c:
            case 0x34:
            case 0x3c:
                Ka = Qa[Fb++];;
                Ma = b >> 3;
                Ob(0, Qb(Ma, Aa[0] & 0xff, Ka));
                break kd;
            case 0x05:
            case 0x0d:
            case 0x15:
            case 0x1d:
            case 0x25:
            case 0x2d:
            case 0x35:
            case 0x3d:
                {
                    Ka = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                    Fb += 4;
                };
                Ma = b >> 3;
                Aa[0] = ac(Ma, Aa[0], Ka);
                break kd;
            case 0x80:
                Ha = Qa[Fb++];;
                Ma = (Ha >> 3) & 7;
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7;
                    Ka = Qa[Fb++];;
                    Ob(Ia, Qb(Ma, ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff), Ka));
                } else {
                    ia = Jb(Ha);
                    Ka = Qa[Fb++];;
                    if (Ma != 7) {
                        ja = hb();
                        ja = Qb(Ma, ja, Ka);
                        nb(ja);
                    } else {
                        ja = bb();
                        Qb(7, ja, Ka);
                    }
                }
                break kd;
            case 0x81:
                Ha = Qa[Fb++];;
                Ma = (Ha >> 3) & 7;
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7; {
                        Ka = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                        Fb += 4;
                    };
                    Aa[Ia] = ac(Ma, Aa[Ia], Ka);
                } else {
                    ia = Jb(Ha); {
                        Ka = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                        Fb += 4;
                    };
                    if (Ma != 7) {
                        ja = lb();
                        ja = ac(Ma, ja, Ka);
                        rb(ja);
                    } else {
                        ja = fb();
                        ac(7, ja, Ka);
                    }
                }
                break kd;
            case 0x83:
                Ha = Qa[Fb++];;
                Ma = (Ha >> 3) & 7;
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7;
                    Ka = ((Qa[Fb++] << 24) >> 24);;
                    Aa[Ia] = ac(Ma, Aa[Ia], Ka);
                } else {
                    ia = Jb(Ha);
                    Ka = ((Qa[Fb++] << 24) >> 24);;
                    if (Ma != 7) {
                        ja = lb();
                        ja = ac(Ma, ja, Ka);
                        rb(ja);
                    } else {
                        ja = fb();
                        ac(7, ja, Ka);
                    }
                }
                break kd;
            case 0x40:
            case 0x41:
            case 0x42:
            case 0x43:
            case 0x44:
            case 0x45:
            case 0x46:
            case 0x47:
                Ja = b & 7; {
                    if (Da < 25) {
                        Ea = Da;
                    }
                    Aa[Ja] = Fa = (Aa[Ja] + 1) & -1;
                    Da = 27;
                };
                break kd;
            case 0x48:
            case 0x49:
            case 0x4a:
            case 0x4b:
            case 0x4c:
            case 0x4d:
            case 0x4e:
            case 0x4f:
                Ja = b & 7; {
                    if (Da < 25) {
                        Ea = Da;
                    }
                    Aa[Ja] = Fa = (Aa[Ja] - 1) & -1;
                    Da = 30;
                };
                break kd;
            case 0x6b:
                Ha = Qa[Fb++];;
                Ja = (Ha >> 3) & 7;
                if ((Ha >> 6) == 3) {
                    Ka = Aa[Ha & 7];
                } else {
                    ia = Jb(Ha);
                    Ka = fb();
                }
                La = ((Qa[Fb++] << 24) >> 24);;
                Aa[Ja] = Lc(Ka, La);
                break kd;
            case 0x69:
                Ha = Qa[Fb++];;
                Ja = (Ha >> 3) & 7;
                if ((Ha >> 6) == 3) {
                    Ka = Aa[Ha & 7];
                } else {
                    ia = Jb(Ha);
                    Ka = fb();
                } {
                    La = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                    Fb += 4;
                };
                Aa[Ja] = Lc(Ka, La);
                break kd;
            case 0x84:
                Ha = Qa[Fb++];;
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7;
                    ja = ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff);
                } else {
                    ia = Jb(Ha);
                    ja = bb();
                }
                Ja = (Ha >> 3) & 7;
                Ka = ((Aa[Ja & 3] >> ((Ja & 4) << 1)) & 0xff);
                Ca = ja & Ka;
                Da = 12;
                break kd;
            case 0x85:
                Ha = Qa[Fb++];;
                if ((Ha >> 6) == 3) {
                    ja = Aa[Ha & 7];
                } else {
                    ia = Jb(Ha);
                    ja = fb();
                }
                Ka = Aa[(Ha >> 3) & 7];
                Ca = ja & Ka;
                Da = 14;
                break kd;
            case 0xa8:
                Ka = Qa[Fb++];;
                Ca = Aa[0] & Ka;
                Da = 12;
                break kd;
            case 0xa9:
                {
                    Ka = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                    Fb += 4;
                };
                Ca = Aa[0] & Ka;
                Da = 14;
                break kd;
            case 0xf6:
                Ha = Qa[Fb++];;
                Ma = (Ha >> 3) & 7;
                switch (Ma) {
                case 0:
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        ja = ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff);
                    } else {
                        ia = Jb(Ha);
                        ja = bb();
                    }
                    Ka = Qa[Fb++];;
                    Ca = ja & Ka;
                    Da = 12;
                    break;
                case 2:
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Ob(Ia, ~ ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff));
                    } else {
                        ia = Jb(Ha);
                        ja = hb();
                        ja = ~ja;
                        nb(ja);
                    }
                    break;
                case 3:
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Ob(Ia, Qb(5, 0, ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff)));
                    } else {
                        ia = Jb(Ha);
                        ja = hb();
                        ja = Qb(5, 0, ja);
                        nb(ja);
                    }
                    break;
                case 4:
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        ja = ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff);
                    } else {
                        ia = Jb(Ha);
                        ja = bb();
                    }
                    Pb(0, Dc(Aa[0], ja));
                    break;
                case 5:
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        ja = ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff);
                    } else {
                        ia = Jb(Ha);
                        ja = bb();
                    }
                    Pb(0, Ec(Aa[0], ja));
                    break;
                case 6:
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        ja = ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff);
                    } else {
                        ia = Jb(Ha);
                        ja = bb();
                    }
                    rc(ja);
                    break;
                case 7:
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        ja = ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff);
                    } else {
                        ia = Jb(Ha);
                        ja = bb();
                    }
                    tc(ja);
                    break;
                default:
                    sc(6);
                }
                break kd;
            case 0xf7:
                Ha = Qa[Fb++];;
                Ma = (Ha >> 3) & 7;
                switch (Ma) {
                case 0:
                    if ((Ha >> 6) == 3) {
                        ja = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        ja = fb();
                    } {
                        Ka = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                        Fb += 4;
                    };
                    Ca = ja & Ka;
                    Da = 14;
                    break;
                case 2:
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Aa[Ia] = ~Aa[Ia];
                    } else {
                        ia = Jb(Ha);
                        ja = lb();
                        ja = ~ja;
                        rb(ja);
                    }
                    break;
                case 3:
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Aa[Ia] = ac(5, 0, Aa[Ia]);
                    } else {
                        ia = Jb(Ha);
                        ja = lb();
                        ja = ac(5, 0, ja);
                        rb(ja);
                    }
                    break;
                case 4:
                    if ((Ha >> 6) == 3) {
                        ja = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        ja = fb();
                    }
                    Aa[0] = Kc(Aa[0], ja);
                    Aa[2] = Pa;
                    break;
                case 5:
                    if ((Ha >> 6) == 3) {
                        ja = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        ja = fb();
                    }
                    Aa[0] = Lc(Aa[0], ja);
                    Aa[2] = Pa;
                    break;
                case 6:
                    if ((Ha >> 6) == 3) {
                        ja = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        ja = fb();
                    }
                    Aa[0] = wc(Aa[2], Aa[0], ja);
                    Aa[2] = Pa;
                    break;
                case 7:
                    if ((Ha >> 6) == 3) {
                        ja = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        ja = fb();
                    }
                    Aa[0] = Ac(Aa[2], Aa[0], ja);
                    Aa[2] = Pa;
                    break;
                default:
                    sc(6);
                }
                break kd;
            case 0xc0:
                Ha = Qa[Fb++];;
                Ma = (Ha >> 3) & 7;
                if ((Ha >> 6) == 3) {
                    Ka = Qa[Fb++];;
                    Ia = Ha & 7;
                    Ob(Ia, dc(Ma, ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff), Ka));
                } else {
                    ia = Jb(Ha);
                    Ka = Qa[Fb++];;
                    ja = hb();
                    ja = dc(Ma, ja, Ka);
                    nb(ja);
                }
                break kd;
            case 0xc1:
                Ha = Qa[Fb++];;
                Ma = (Ha >> 3) & 7;
                if ((Ha >> 6) == 3) {
                    Ka = Qa[Fb++];;
                    Ia = Ha & 7;
                    Aa[Ia] = hc(Ma, Aa[Ia], Ka);
                } else {
                    ia = Jb(Ha);
                    Ka = Qa[Fb++];;
                    ja = lb();
                    ja = hc(Ma, ja, Ka);
                    rb(ja);
                }
                break kd;
            case 0xd0:
                Ha = Qa[Fb++];;
                Ma = (Ha >> 3) & 7;
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7;
                    Ob(Ia, dc(Ma, ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff), 1));
                } else {
                    ia = Jb(Ha);
                    ja = hb();
                    ja = dc(Ma, ja, 1);
                    nb(ja);
                }
                break kd;
            case 0xd1:
                Ha = Qa[Fb++];;
                Ma = (Ha >> 3) & 7;
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7;
                    Aa[Ia] = hc(Ma, Aa[Ia], 1);
                } else {
                    ia = Jb(Ha);
                    ja = lb();
                    ja = hc(Ma, ja, 1);
                    rb(ja);
                }
                break kd;
            case 0xd2:
                Ha = Qa[Fb++];;
                Ma = (Ha >> 3) & 7;
                Ka = Aa[1] & 0xff;
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7;
                    Ob(Ia, dc(Ma, ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff), Ka));
                } else {
                    ia = Jb(Ha);
                    ja = hb();
                    ja = dc(Ma, ja, Ka);
                    nb(ja);
                }
                break kd;
            case 0xd3:
                Ha = Qa[Fb++];;
                Ma = (Ha >> 3) & 7;
                Ka = Aa[1] & 0xff;
                if ((Ha >> 6) == 3) {
                    Ia = Ha & 7;
                    Aa[Ia] = hc(Ma, Aa[Ia], Ka);
                } else {
                    ia = Jb(Ha);
                    ja = lb();
                    ja = hc(Ma, ja, Ka);
                    rb(ja);
                }
                break kd;
            case 0x98:
                Aa[0] = (Aa[0] << 16) >> 16;
                break kd;
            case 0x99:
                Aa[2] = Aa[0] >> 31;
                break kd;
            case 0x50:
            case 0x51:
            case 0x52:
            case 0x53:
            case 0x54:
            case 0x55:
            case 0x56:
            case 0x57:
                ja = Aa[b & 7];
                ia = (Aa[4] - 4) & -1; {
                    Ra = Xa[ia >>> 12];
                    if ((Ra | ia) & 3) {
                        qb(ja);
                    } else {
                        Ra ^= ia;
                        Qa[Ra] = ja & 0xff;
                        Qa[Ra + 1] = (ja >> 8) & 0xff;
                        Qa[Ra + 2] = (ja >> 16) & 0xff;
                        Qa[Ra + 3] = (ja >> 24) & 0xff;
                    }
                };
                Aa[4] = ia;
                break kd;
            case 0x58:
            case 0x59:
            case 0x5a:
            case 0x5b:
            case 0x5c:
            case 0x5d:
            case 0x5e:
            case 0x5f:
                ia = Aa[4];
                ja = (((Ra = Wa[ia >>> 12]) | ia) & 3 ? eb() : (Ra ^= ia, Qa[Ra] | (Qa[Ra + 1] << 8) | (Qa[Ra + 2] << 16) | (Qa[Ra + 3] << 24)));
                Aa[4] = (ia + 4) & -1;
                Aa[b & 7] = ja;
                break kd;
            case 0x60:
                ia = (Aa[4] - 32) & -1;
                Ka = ia;
                for (Ja = 7; Ja >= 0; Ja--) {
                    ja = Aa[Ja]; {
                        Ra = Xa[ia >>> 12];
                        if ((Ra | ia) & 3) {
                            qb(ja);
                        } else {
                            Ra ^= ia;
                            Qa[Ra] = ja & 0xff;
                            Qa[Ra + 1] = (ja >> 8) & 0xff;
                            Qa[Ra + 2] = (ja >> 16) & 0xff;
                            Qa[Ra + 3] = (ja >> 24) & 0xff;
                        }
                    };
                    ia = (ia + 4) & -1;
                }
                Aa[4] = Ka;
                break kd;
            case 0x61:
                ia = Aa[4];
                for (Ja = 7; Ja >= 0; Ja--) {
                    if (Ja != 4) {
                        Aa[Ja] = (((Ra = Wa[ia >>> 12]) | ia) & 3 ? eb() : (Ra ^= ia, Qa[Ra] | (Qa[Ra + 1] << 8) | (Qa[Ra + 2] << 16) | (Qa[Ra + 3] << 24)));
                    }
                    ia = (ia + 4) & -1;
                }
                Aa[4] = ia;
                break kd;
            case 0x8f:
                Ha = Qa[Fb++];;
                if ((Ha >> 6) == 3) {
                    ia = Aa[4];
                    ja = fb();
                    Aa[4] = (ia + 4) & -1;
                    Aa[Ha & 7] = ja;
                } else {
                    ia = Aa[4];
                    ja = fb();
                    ia = Jb(Ha, 4);
                    rb(ja);
                    Aa[4] = (Aa[4] + 4) & -1;
                }
                break kd;
            case 0x68:
                {
                    ja = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                    Fb += 4;
                };
                ia = (Aa[4] - 4) & -1;
                rb(ja);
                Aa[4] = ia;
                break kd;
            case 0x6a:
                ja = ((Qa[Fb++] << 24) >> 24);;
                ia = (Aa[4] - 4) & -1;
                rb(ja);
                Aa[4] = ia;
                break kd;
            case 0xc9:
                ia = Aa[5];
                ja = fb();
                Aa[5] = ja;
                Aa[4] = (ia + 4) & -1;
                break kd;
            case 0x9c:
                ja = Qc();
                ia = (Aa[4] - 4) & -1;
                rb(ja);
                Aa[4] = ia;
                break kd;
            case 0x9d:
                ia = Aa[4];
                ja = fb();
                Aa[4] = (ia + 4) & -1;
                if (za.cpl == 0) {
                    Sc(ja, (0x00000100 | 0x00040000 | 0x00200000 | 0x00004000 | 0x00000200 | 0x00003000)); {
                        if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                    };
                } else {
                    var ze;
                    ze = (za.eflags >> 12) & 3;
                    if (za.cpl <= ze) {
                        Sc(ja, (0x00000100 | 0x00040000 | 0x00200000 | 0x00004000 | 0x00000200)); {
                            if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                        };
                    } else {
                        Sc(ja, (0x00000100 | 0x00040000 | 0x00200000 | 0x00004000));
                    }
                }
                break kd;
            case 0x06:
                {
                    ja = za.segs[0].selector;
                    ia = (Aa[4] - 4) & -1;
                    rb(ja);
                    Aa[4] = ia;
                };
                break kd;
            case 0x0e:
                {
                    ja = za.segs[1].selector;
                    ia = (Aa[4] - 4) & -1;
                    rb(ja);
                    Aa[4] = ia;
                };
                break kd;
            case 0x16:
                {
                    ja = za.segs[2].selector;
                    ia = (Aa[4] - 4) & -1;
                    rb(ja);
                    Aa[4] = ia;
                };
                break kd;
            case 0x1e:
                {
                    ja = za.segs[3].selector;
                    ia = (Aa[4] - 4) & -1;
                    rb(ja);
                    Aa[4] = ia;
                };
                break kd;
            case 0x07:
                {
                    ia = Aa[4];
                    ja = fb();
                    he(0, ja & 0xffff);
                    Aa[4] = (Aa[4] + 4) & -1;
                };
                break kd;
            case 0x17:
                {
                    ia = Aa[4];
                    ja = fb();
                    he(2, ja & 0xffff);
                    Aa[4] = (Aa[4] + 4) & -1;
                };
                break kd;
            case 0x1f:
                {
                    ia = Aa[4];
                    ja = fb();
                    he(3, ja & 0xffff);
                    Aa[4] = (Aa[4] + 4) & -1;
                };
                break kd;
            case 0x8d:
                Ha = Qa[Fb++];;
                if ((Ha >> 6) == 3) sc(6);
                Ga &= ~0x000f;
                Aa[(Ha >> 3) & 7] = Jb(Ha);
                break kd;
            case 0xfe:
                Ha = Qa[Fb++];;
                Ma = (Ha >> 3) & 7;
                switch (Ma) {
                case 0:
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Ob(Ia, Vb(((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff)));
                    } else {
                        ia = Jb(Ha);
                        ja = hb();
                        ja = Vb(ja);
                        nb(ja);
                    }
                    break;
                case 1:
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Ob(Ia, Wb(((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff)));
                    } else {
                        ia = Jb(Ha);
                        ja = hb();
                        ja = Wb(ja);
                        nb(ja);
                    }
                    break;
                default:
                    sc(6);
                }
                break kd;
            case 0xff:
                Ha = Qa[Fb++];;
                Ma = (Ha >> 3) & 7;
                switch (Ma) {
                case 0:
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7; {
                            if (Da < 25) {
                                Ea = Da;
                            }
                            Aa[Ia] = Fa = (Aa[Ia] + 1) & -1;
                            Da = 27;
                        };
                    } else {
                        ia = Jb(Ha);
                        ja = lb(); {
                            if (Da < 25) {
                                Ea = Da;
                            }
                            ja = Fa = (ja + 1) & -1;
                            Da = 27;
                        };
                        rb(ja);
                    }
                    break;
                case 1:
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7; {
                            if (Da < 25) {
                                Ea = Da;
                            }
                            Aa[Ia] = Fa = (Aa[Ia] - 1) & -1;
                            Da = 30;
                        };
                    } else {
                        ia = Jb(Ha);
                        ja = lb(); {
                            if (Da < 25) {
                                Ea = Da;
                            }
                            ja = Fa = (ja - 1) & -1;
                            Da = 30;
                        };
                        rb(ja);
                    }
                    break;
                case 2:
                    if ((Ha >> 6) == 3) {
                        ja = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        ja = fb();
                    }
                    ia = (Aa[4] - 4) & -1;
                    rb((Eb + Fb - Hb));
                    Aa[4] = ia;
                    Eb = ja, Fb = Hb = 0;
                    break;
                case 4:
                    if ((Ha >> 6) == 3) {
                        ja = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        ja = fb();
                    }
                    Eb = ja, Fb = Hb = 0;
                    break;
                case 6:
                    if ((Ha >> 6) == 3) {
                        ja = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        ja = fb();
                    }
                    ia = (Aa[4] - 4) & -1;
                    rb(ja);
                    Aa[4] = ia;
                    break;
                case 3:
                case 5:
                default:
                    throw "GRP5";
                }
                break kd;
            case 0xeb:
                ja = ((Qa[Fb++] << 24) >> 24);;
                Fb = (Fb + ja) >> 0;
                break kd;
            case 0xe9:
                {
                    ja = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                    Fb += 4;
                };
                Fb = (Fb + ja) >> 0;
                break kd;
            case 0xea:
                {
                    ja = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                    Fb += 4;
                };
                Ka = Ib();
                ke(Ka, ja);
                break kd;
            case 0x70:
            case 0x71:
            case 0x72:
            case 0x73:
            case 0x76:
            case 0x77:
            case 0x78:
            case 0x79:
            case 0x7a:
            case 0x7b:
            case 0x7c:
            case 0x7d:
            case 0x7e:
            case 0x7f:
                if (Ub(b & 0xf)) {
                    ja = ((Qa[Fb++] << 24) >> 24);;
                    Fb = (Fb + ja) >> 0;
                } else {
                    Fb = (Fb + 1) >> 0;
                }
                break kd;
            case 0x74:
                switch (Da) {
                case 0:
                case 3:
                case 6:
                case 9:
                case 12:
                case 15:
                case 18:
                case 21:
                    Ka = (Ca & 0xff) == 0;
                    break;
                case 1:
                case 4:
                case 7:
                case 10:
                case 13:
                case 16:
                case 19:
                case 22:
                    Ka = (Ca & 0xffff) == 0;
                    break;
                case 2:
                case 5:
                case 8:
                case 11:
                case 14:
                case 17:
                case 20:
                case 23:
                    Ka = Ca == 0;
                    break;
                case 24:
                    Ka = (Ba >> 6) & 1;
                    break;
                case 25:
                case 28:
                    Ka = (Fa & 0xff) == 0;
                    break;
                case 26:
                case 29:
                    Ka = (Fa & 0xffff) == 0;
                    break;
                case 27:
                case 30:
                    Ka = Fa == 0;
                    break;
                default:
                    throw "JZ: unsupported cc_op=" + Da;
                };
                if (Ka) {
                    ja = ((Qa[Fb++] << 24) >> 24);;
                    Fb = (Fb + ja) >> 0;
                } else {
                    Fb = (Fb + 1) >> 0;
                }
                break kd;
            case 0x75:
                switch (Da) {
                case 0:
                case 3:
                case 6:
                case 9:
                case 12:
                case 15:
                case 18:
                case 21:
                    Ka = (Ca & 0xff) == 0;
                    break;
                case 1:
                case 4:
                case 7:
                case 10:
                case 13:
                case 16:
                case 19:
                case 22:
                    Ka = (Ca & 0xffff) == 0;
                    break;
                case 2:
                case 5:
                case 8:
                case 11:
                case 14:
                case 17:
                case 20:
                case 23:
                    Ka = Ca == 0;
                    break;
                case 24:
                    Ka = (Ba >> 6) & 1;
                    break;
                case 25:
                case 28:
                    Ka = (Fa & 0xff) == 0;
                    break;
                case 26:
                case 29:
                    Ka = (Fa & 0xffff) == 0;
                    break;
                case 27:
                case 30:
                    Ka = Fa == 0;
                    break;
                default:
                    throw "JZ: unsupported cc_op=" + Da;
                };
                if (!Ka) {
                    ja = ((Qa[Fb++] << 24) >> 24);;
                    Fb = (Fb + ja) >> 0;
                } else {
                    Fb = (Fb + 1) >> 0;
                }
                break kd;
            case 0xe2:
                ja = ((Qa[Fb++] << 24) >> 24);;
                Aa[1] = (Aa[1] - 1) & -1;
                if (Aa[1]) Fb = (Fb + ja) >> 0;
                break kd;
            case 0xe3:
                ja = ((Qa[Fb++] << 24) >> 24);;
                if (Aa[1] == 0) Fb = (Fb + ja) >> 0;
                break kd;
            case 0xc2:
                Ka = (Ib() << 16) >> 16;
                ia = Aa[4];
                ja = fb();
                Aa[4] = (Aa[4] + 4 + Ka) & -1;
                Eb = ja, Fb = Hb = 0;
                break kd;
            case 0xc3:
                ia = Aa[4];
                ja = fb();
                Aa[4] = (Aa[4] + 4) & -1;
                Eb = ja, Fb = Hb = 0;
                break kd;
            case 0xe8:
                {
                    ja = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                    Fb += 4;
                };
                ia = (Aa[4] - 4) & -1;
                rb((Eb + Fb - Hb));
                Aa[4] = ia;
                Fb = (Fb + ja) >> 0;
                break kd;
            case 0x90:
                break kd;
            case 0xcc:
                Ka = (Eb + Fb - Hb);
                Pd(3, 1, 0, Ka, 0);
                break kd;
            case 0xcd:
                ja = Qa[Fb++];;
                Ka = (Eb + Fb - Hb);
                Pd(ja, 1, 0, Ka, 0);
                break kd;
            case 0xce:
                if (Ub(0)) {
                    Ka = (Eb + Fb - Hb);
                    Pd(4, 1, 0, Ka, 0);
                }
                break kd;
            case 0x62:
                Qe();
                break kd;
            case 0xcf:
                Ce(1); {
                    if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                };
                break kd;
            case 0xf5:
                Ba = fc() ^ 0x0001;
                Da = 24;
                break kd;
            case 0xf8:
                Ba = fc() & ~0x0001;
                Da = 24;
                break kd;
            case 0xf9:
                Ba = fc() | 0x0001;
                Da = 24;
                break kd;
            case 0xfc:
                za.df = 1;
                break kd;
            case 0xfd:
                za.df = -1;
                break kd;
            case 0xfa:
                ze = (za.eflags >> 12) & 3;
                if (za.cpl > ze) sc(13);
                za.eflags &= ~0x00000200;
                break kd;
            case 0xfb:
                ze = (za.eflags >> 12) & 3;
                if (za.cpl > ze) sc(13);
                za.eflags |= 0x00000200; {
                    if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                };
                break kd;
            case 0x9e:
                ja = ((Aa[0] >> 8) & (0x0080 | 0x0040 | 0x0010 | 0x0004 | 0x0001)) | (Ub(0) << 11);
                Ba = ja;
                Da = 24;
                break kd;
            case 0x9f:
                ja = Qc();
                Ob(4, ja);
                break kd;
            case 0xf4:
                if (za.cpl != 0) sc(13);
                za.halted = 1;
                Oa = 257;
                break Se;
            case 0xa4:
                if (Ga & (0x0010 | 0x0020)) {
                    if (Aa[1]) {
                        if (8 === 32 && (Aa[1] >>> 0) >= 4 && za.df == 1 && ((Aa[6] | Aa[7]) & 3) == 0 && cd()) {} else {
                            ia = Aa[6];
                            ja = bb();
                            ia = Aa[7];
                            nb(ja);
                            Aa[6] = (Aa[6] + (za.df << 0)) & -1;
                            Aa[7] = (Aa[7] + (za.df << 0)) & -1;
                            Aa[1] = (Aa[1] - 1) & -1;
                        }
                        Fb = Hb;
                    }
                } else {
                    ia = Aa[6];
                    ja = bb();
                    ia = Aa[7];
                    nb(ja);
                    Aa[6] = (Aa[6] + (za.df << 0)) & -1;
                    Aa[7] = (Aa[7] + (za.df << 0)) & -1;
                };
                break kd;
            case 0xa5:
                if (Ga & (0x0010 | 0x0020)) {
                    if (Aa[1]) {
                        if (32 === 32 && (Aa[1] >>> 0) >= 4 && za.df == 1 && ((Aa[6] | Aa[7]) & 3) == 0 && cd()) {} else {
                            ia = Aa[6];
                            ja = fb();
                            ia = Aa[7];
                            rb(ja);
                            Aa[6] = (Aa[6] + (za.df << 2)) & -1;
                            Aa[7] = (Aa[7] + (za.df << 2)) & -1;
                            Aa[1] = (Aa[1] - 1) & -1;
                        }
                        Fb = Hb;
                    }
                } else {
                    ia = Aa[6];
                    ja = fb();
                    ia = Aa[7];
                    rb(ja);
                    Aa[6] = (Aa[6] + (za.df << 2)) & -1;
                    Aa[7] = (Aa[7] + (za.df << 2)) & -1;
                };
                break kd;
            case 0xaa:
                if (Ga & (0x0010 | 0x0020)) {
                    if (Aa[1]) {
                        if (8 === 32 && (Aa[1] >>> 0) >= 4 && za.df == 1 && (Aa[7] & 3) == 0 && hd()) {} else {
                            ia = Aa[7];
                            nb(Aa[0]);
                            Aa[7] = (ia + (za.df << 0)) & -1;
                            Aa[1] = (Aa[1] - 1) & -1;
                        }
                        Fb = Hb;
                    }
                } else {
                    ia = Aa[7];
                    nb(Aa[0]);
                    Aa[7] = (ia + (za.df << 0)) & -1;
                };
                break kd;
            case 0xab:
                if (Ga & (0x0010 | 0x0020)) {
                    if (Aa[1]) {
                        if (32 === 32 && (Aa[1] >>> 0) >= 4 && za.df == 1 && (Aa[7] & 3) == 0 && hd()) {} else {
                            ia = Aa[7];
                            rb(Aa[0]);
                            Aa[7] = (ia + (za.df << 2)) & -1;
                            Aa[1] = (Aa[1] - 1) & -1;
                        }
                        Fb = Hb;
                    }
                } else {
                    ia = Aa[7];
                    rb(Aa[0]);
                    Aa[7] = (ia + (za.df << 2)) & -1;
                };
                break kd;
            case 0xa6:
                if (Ga & (0x0010 | 0x0020)) {
                    if (Aa[1]) {
                        ia = Aa[6];
                        ja = bb();
                        ia = Aa[7];
                        Ka = bb();
                        Qb(7, ja, Ka);
                        Aa[6] = (Aa[6] + (za.df << 0)) & -1;
                        Aa[7] = (Aa[7] + (za.df << 0)) & -1;
                        Aa[1] = (Aa[1] - 1) & -1;
                        if (Ga & 0x0010) {
                            if (!Ub(4)) break kd;
                        } else {
                            if (Ub(4)) break kd;
                        }
                        Fb = Hb;
                    }
                } else {
                    ia = Aa[6];
                    ja = bb();
                    ia = Aa[7];
                    Ka = bb();
                    Qb(7, ja, Ka);
                    Aa[6] = (Aa[6] + (za.df << 0)) & -1;
                    Aa[7] = (Aa[7] + (za.df << 0)) & -1;
                };
                break kd;
            case 0xa7:
                if (Ga & (0x0010 | 0x0020)) {
                    if (Aa[1]) {
                        ia = Aa[6];
                        ja = fb();
                        ia = Aa[7];
                        Ka = fb();
                        ac(7, ja, Ka);
                        Aa[6] = (Aa[6] + (za.df << 2)) & -1;
                        Aa[7] = (Aa[7] + (za.df << 2)) & -1;
                        Aa[1] = (Aa[1] - 1) & -1;
                        if (Ga & 0x0010) {
                            if (!Ub(4)) break kd;
                        } else {
                            if (Ub(4)) break kd;
                        }
                        Fb = Hb;
                    }
                } else {
                    ia = Aa[6];
                    ja = fb();
                    ia = Aa[7];
                    Ka = fb();
                    ac(7, ja, Ka);
                    Aa[6] = (Aa[6] + (za.df << 2)) & -1;
                    Aa[7] = (Aa[7] + (za.df << 2)) & -1;
                };
                break kd;
            case 0xac:
                if (Ga & (0x0010 | 0x0020)) {
                    if (Aa[1]) {
                        ia = Aa[6];
                        if (8 == 32) Aa[0] = fb();
                        else Ob(0, bb());
                        Aa[6] = (ia + (za.df << 0)) & -1;
                        Aa[1] = (Aa[1] - 1) & -1;
                        Fb = Hb;
                    }
                } else {
                    ia = Aa[6];
                    if (8 == 32) Aa[0] = fb();
                    else Ob(0, bb());
                    Aa[6] = (ia + (za.df << 0)) & -1;
                };
                break kd;
            case 0xad:
                if (Ga & (0x0010 | 0x0020)) {
                    if (Aa[1]) {
                        ia = Aa[6];
                        if (32 == 32) Aa[0] = fb();
                        else Ue(0, fb());
                        Aa[6] = (ia + (za.df << 2)) & -1;
                        Aa[1] = (Aa[1] - 1) & -1;
                        Fb = Hb;
                    }
                } else {
                    ia = Aa[6];
                    if (32 == 32) Aa[0] = fb();
                    else Ue(0, fb());
                    Aa[6] = (ia + (za.df << 2)) & -1;
                };
                break kd;
            case 0xae:
                if (Ga & (0x0010 | 0x0020)) {
                    if (Aa[1]) {
                        ia = Aa[7];
                        ja = bb();
                        Qb(7, Aa[0], ja);
                        Aa[7] = (Aa[7] + (za.df << 0)) & -1;
                        Aa[1] = (Aa[1] - 1) & -1;
                        if (Ga & 0x0010) {
                            if (!Ub(4)) break kd;
                        } else {
                            if (Ub(4)) break kd;
                        }
                        Fb = Hb;
                    }
                } else {
                    ia = Aa[7];
                    ja = bb();
                    Qb(7, Aa[0], ja);
                    Aa[7] = (Aa[7] + (za.df << 0)) & -1;
                };
                break kd;
            case 0xaf:
                if (Ga & (0x0010 | 0x0020)) {
                    if (Aa[1]) {
                        ia = Aa[7];
                        ja = fb();
                        ac(7, Aa[0], ja);
                        Aa[7] = (Aa[7] + (za.df << 2)) & -1;
                        Aa[1] = (Aa[1] - 1) & -1;
                        if (Ga & 0x0010) {
                            if (!Ub(4)) break kd;
                        } else {
                            if (Ub(4)) break kd;
                        }
                        Fb = Hb;
                    }
                } else {
                    ia = Aa[7];
                    ja = fb();
                    ac(7, Aa[0], ja);
                    Aa[7] = (Aa[7] + (za.df << 2)) & -1;
                };
                break kd;
            case 0xd8:
            case 0xd9:
            case 0xda:
            case 0xdb:
            case 0xdc:
            case 0xdd:
            case 0xde:
            case 0xdf:
                if (za.cr0 & ((1 << 2) | (1 << 3))) {
                    sc(7);
                }
                Ha = Qa[Fb++];;
                Ja = (Ha >> 3) & 7;
                Ia = Ha & 7;
                Ma = ((b & 7) << 3) | ((Ha >> 3) & 7);
                Pb(0, 0xffff);
                if ((Ha >> 6) == 3) {} else {
                    ia = Jb(Ha);
                }
                break kd;
            case 0x9b:
                break kd;
            case 0xe4:
                ze = (za.eflags >> 12) & 3;
                if (za.cpl > ze) sc(13);
                ja = Qa[Fb++];;
                Ob(0, za.ld8_port(ja)); {
                    if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                };
                break kd;
            case 0xe5:
                ze = (za.eflags >> 12) & 3;
                if (za.cpl > ze) sc(13);
                ja = Qa[Fb++];;
                Aa[0] = za.ld32_port(ja); {
                    if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                };
                break kd;
            case 0xe6:
                ze = (za.eflags >> 12) & 3;
                if (za.cpl > ze) sc(13);
                ja = Qa[Fb++];;
                za.st8_port(ja, Aa[0] & 0xff); {
                    if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                };
                break kd;
            case 0xe7:
                ze = (za.eflags >> 12) & 3;
                if (za.cpl > ze) sc(13);
                ja = Qa[Fb++];;
                za.st32_port(ja, Aa[0]); {
                    if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                };
                break kd;
            case 0xec:
                ze = (za.eflags >> 12) & 3;
                if (za.cpl > ze) sc(13);
                Ob(0, za.ld8_port(Aa[2] & 0xffff)); {
                    if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                };
                break kd;
            case 0xed:
                ze = (za.eflags >> 12) & 3;
                if (za.cpl > ze) sc(13);
                Aa[0] = za.ld32_port(Aa[2] & 0xffff); {
                    if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                };
                break kd;
            case 0xee:
                ze = (za.eflags >> 12) & 3;
                if (za.cpl > ze) sc(13);
                za.st8_port(Aa[2] & 0xffff, Aa[0] & 0xff); {
                    if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                };
                break kd;
            case 0xef:
                ze = (za.eflags >> 12) & 3;
                if (za.cpl > ze) sc(13);
                za.st32_port(Aa[2] & 0xffff, Aa[0]); {
                    if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                };
                break kd;
            case 0x27:
                Me();
                break kd;
            case 0x2f:
                Oe();
                break kd;
            case 0x37:
                Ie();
                break kd;
            case 0x3f:
                Le();
                break kd;
            case 0xd4:
                ja = Qa[Fb++];;
                Ee(ja);
                break kd;
            case 0xd5:
                ja = Qa[Fb++];;
                He(ja);
                break kd;
            case 0x26:
            case 0x2e:
            case 0x36:
            case 0x3e:
            case 0x63:
            case 0x67:
            case 0x6c:
            case 0x6d:
            case 0x6e:
            case 0x6f:
            case 0x82:
            case 0x9a:
            case 0xc8:
            case 0xca:
            case 0xcb:
            case 0xd6:
            case 0xe0:
            case 0xe1:
            case 0xf1:
                sc(6);
                break;
            case 0x0f:
                b = Qa[Fb++];;
                switch (b) {
                case 0x80:
                case 0x81:
                case 0x82:
                case 0x83:
                case 0x84:
                case 0x85:
                case 0x86:
                case 0x87:
                case 0x88:
                case 0x89:
                case 0x8a:
                case 0x8b:
                case 0x8c:
                case 0x8d:
                case 0x8e:
                case 0x8f:
                    Ka = Ub(b & 0xf); {
                        ja = Qa[Fb] | (Qa[Fb + 1] << 8) | (Qa[Fb + 2] << 16) | (Qa[Fb + 3] << 24);
                        Fb += 4;
                    };
                    if (Ka) Fb = (Fb + ja) >> 0;
                    break kd;
                case 0x90:
                case 0x91:
                case 0x92:
                case 0x93:
                case 0x94:
                case 0x95:
                case 0x96:
                case 0x97:
                case 0x98:
                case 0x99:
                case 0x9a:
                case 0x9b:
                case 0x9c:
                case 0x9d:
                case 0x9e:
                case 0x9f:
                    Ha = Qa[Fb++];;
                    ja = Ub(b & 0xf);
                    if ((Ha >> 6) == 3) {
                        Ob(Ha & 7, ja);
                    } else {
                        ia = Jb(Ha);
                        nb(ja);
                    }
                    break kd;
                case 0x40:
                case 0x41:
                case 0x42:
                case 0x43:
                case 0x44:
                case 0x45:
                case 0x46:
                case 0x47:
                case 0x48:
                case 0x49:
                case 0x4a:
                case 0x4b:
                case 0x4c:
                case 0x4d:
                case 0x4e:
                case 0x4f:
                    Ha = Qa[Fb++];;
                    if ((Ha >> 6) == 3) {
                        ja = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        ja = fb();
                    }
                    if (Ub(b & 0xf)) Aa[(Ha >> 3) & 7] = ja;
                    break kd;
                case 0xb6:
                    Ha = Qa[Fb++];;
                    Ja = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        ja = ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff);
                    } else {
                        ia = Jb(Ha);
                        ja = bb();
                    }
                    Aa[Ja] = ja;
                    break kd;
                case 0xb7:
                    Ha = Qa[Fb++];;
                    Ja = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        ja = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        ja = db();
                    }
                    Aa[Ja] = ja & 0xffff;
                    break kd;
                case 0xbe:
                    Ha = Qa[Fb++];;
                    Ja = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        ja = ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff);
                    } else {
                        ia = Jb(Ha);
                        ja = bb();
                    }
                    Aa[Ja] = (ja << 24) >> 24;
                    break kd;
                case 0xbf:
                    Ha = Qa[Fb++];;
                    Ja = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        ja = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        ja = db();
                    }
                    Aa[Ja] = (ja << 16) >> 16;
                    break kd;
                case 0x00:
                    Ha = Qa[Fb++];;
                    Ma = (Ha >> 3) & 7;
                    switch (Ma) {
                    case 0:
                    case 1:
                        if (Ma == 0) ja = za.ldt.selector;
                        else ja = za.tr.selector;
                        if ((Ha >> 6) == 3) {
                            Pb(Ha & 7, ja);
                        } else {
                            ia = Jb(Ha);
                            pb(ja);
                        }
                        break;
                    case 2:
                        if (za.cpl != 0) sc(13);
                        if ((Ha >> 6) == 3) {
                            ja = Aa[Ha & 7] & 0xffff;
                        } else {
                            ia = Jb(Ha);
                            ja = db();
                        }
                        ee(ja);
                        break;
                    case 3:
                        if (za.cpl != 0) sc(13);
                        if ((Ha >> 6) == 3) {
                            ja = Aa[Ha & 7] & 0xffff;
                        } else {
                            ia = Jb(Ha);
                            ja = db();
                        }
                        ge(ja);
                        break;
                    default:
                        sc(6);
                    }
                    break kd;
                case 0x01:
                    Ha = Qa[Fb++];;
                    Ma = (Ha >> 3) & 7;
                    switch (Ma) {
                    case 2:
                    case 3:
                        if ((Ha >> 6) == 3) sc(6);
                        if (this.cpl != 0) sc(13);
                        ia = Jb(Ha);
                        ja = db();
                        ia += 2;
                        Ka = fb();
                        if (Ma == 2) {
                            this.gdt.base = Ka;
                            this.gdt.limit = ja;
                        } else {
                            this.idt.base = Ka;
                            this.idt.limit = ja;
                        }
                        break;
                    case 7:
                        if (this.cpl != 0) sc(13);
                        if ((Ha >> 6) == 3) sc(6);
                        ia = Jb(Ha);
                        za.tlb_flush_page(ia & -4096);
                        break;
                    default:
                        sc(6);
                    }
                    break kd;
                case 0x20:
                    if (za.cpl != 0) sc(13);
                    Ha = Qa[Fb++];;
                    if ((Ha >> 6) != 3) sc(6);
                    Ja = (Ha >> 3) & 7;
                    switch (Ja) {
                    case 0:
                        ja = za.cr0;
                        break;
                    case 2:
                        ja = za.cr2;
                        break;
                    case 3:
                        ja = za.cr3;
                        break;
                    case 4:
                        ja = za.cr4;
                        break;
                    default:
                        sc(6);
                    }
                    Aa[Ha & 7] = ja;
                    break kd;
                case 0x22:
                    if (za.cpl != 0) sc(13);
                    Ha = Qa[Fb++];;
                    if ((Ha >> 6) != 3) sc(6);
                    Ja = (Ha >> 3) & 7;
                    ja = Aa[Ha & 7];
                    switch (Ja) {
                    case 0:
                        ud(ja);
                        break;
                    case 2:
                        za.cr2 = ja;
                        break;
                    case 3:
                        wd(ja);
                        break;
                    case 4:
                        yd(ja);
                        break;
                    default:
                        sc(6);
                    }
                    break kd;
                case 0x06:
                    if (za.cpl != 0) sc(13);
                    ud(za.cr0 & ~ (1 << 3));
                    break kd;
                case 0x23:
                    if (za.cpl != 0) sc(13);
                    Ha = Qa[Fb++];;
                    if ((Ha >> 6) != 3) sc(6);
                    Ja = (Ha >> 3) & 7;
                    ja = Aa[Ha & 7];
                    if (Ja == 4 || Ja == 5) sc(6);
                    break kd;
                case 0xb2:
                    {
                        Ha = Qa[Fb++];;
                        if ((Ha >> 3) == 3) sc(6);
                        ia = Jb(Ha);
                        ja = fb();
                        ia += 4;
                        Ka = db();
                        he(2, Ka);
                        Aa[(Ha >> 3) & 7] = ja;
                    };
                    break kd;
                case 0xb4:
                    {
                        Ha = Qa[Fb++];;
                        if ((Ha >> 3) == 3) sc(6);
                        ia = Jb(Ha);
                        ja = fb();
                        ia += 4;
                        Ka = db();
                        he(4, Ka);
                        Aa[(Ha >> 3) & 7] = ja;
                    };
                    break kd;
                case 0xb5:
                    {
                        Ha = Qa[Fb++];;
                        if ((Ha >> 3) == 3) sc(6);
                        ia = Jb(Ha);
                        ja = fb();
                        ia += 4;
                        Ka = db();
                        he(5, Ka);
                        Aa[(Ha >> 3) & 7] = ja;
                    };
                    break kd;
                case 0xa2:
                    De();
                    break kd;
                case 0xa4:
                    Ha = Qa[Fb++];;
                    Ka = Aa[(Ha >> 3) & 7];
                    if ((Ha >> 6) == 3) {
                        La = Qa[Fb++];;
                        Ia = Ha & 7;
                        Aa[Ia] = ic(Aa[Ia], Ka, La);
                    } else {
                        ia = Jb(Ha);
                        La = Qa[Fb++];;
                        ja = lb();
                        ja = ic(Ma, ja, Ka, La);
                        rb(ja);
                    }
                    break kd;
                case 0xa5:
                    Ha = Qa[Fb++];;
                    Ka = Aa[(Ha >> 3) & 7];
                    La = Aa[1];
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Aa[Ia] = ic(Aa[Ia], Ka, La);
                    } else {
                        ia = Jb(Ha);
                        ja = lb();
                        ja = ic(Ma, ja, Ka, La);
                        rb(ja);
                    }
                    break kd;
                case 0xac:
                    Ha = Qa[Fb++];;
                    Ka = Aa[(Ha >> 3) & 7];
                    if ((Ha >> 6) == 3) {
                        La = Qa[Fb++];;
                        Ia = Ha & 7;
                        Aa[Ia] = kc(Aa[Ia], Ka, La);
                    } else {
                        ia = Jb(Ha);
                        La = Qa[Fb++];;
                        ja = lb();
                        ja = kc(Ma, ja, Ka, La);
                        rb(ja);
                    }
                    break kd;
                case 0xad:
                    Ha = Qa[Fb++];;
                    Ka = Aa[(Ha >> 3) & 7];
                    La = Aa[1];
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Aa[Ia] = kc(Aa[Ia], Ka, La);
                    } else {
                        ia = Jb(Ha);
                        ja = lb();
                        ja = kc(Ma, ja, Ka, La);
                        rb(ja);
                    }
                    break kd;
                case 0xba:
                    Ha = Qa[Fb++];;
                    Ma = (Ha >> 3) & 7;
                    switch (Ma) {
                    case 4:
                        if ((Ha >> 6) == 3) {
                            ja = Aa[Ha & 7];
                            Ka = Qa[Fb++];;
                        } else {
                            ia = Jb(Ha);
                            Ka = Qa[Fb++];;
                            ja = lb();
                        }
                        lc(ja, Ka);
                        break;
                    case 5:
                        if ((Ha >> 6) == 3) {
                            Ia = Ha & 7;
                            Ka = Qa[Fb++];;
                            Aa[Ia] = mc(Aa[Ia], Ka);
                        } else {
                            ia = Jb(Ha);
                            Ka = Qa[Fb++];;
                            ja = lb();
                            ja = mc(ja, Ka);
                            rb(ja);
                        };
                        break;
                    case 6:
                        if ((Ha >> 6) == 3) {
                            Ia = Ha & 7;
                            Ka = Qa[Fb++];;
                            Aa[Ia] = nc(Aa[Ia], Ka);
                        } else {
                            ia = Jb(Ha);
                            Ka = Qa[Fb++];;
                            ja = lb();
                            ja = nc(ja, Ka);
                            rb(ja);
                        };
                        break;
                    case 7:
                        if ((Ha >> 6) == 3) {
                            Ia = Ha & 7;
                            Ka = Qa[Fb++];;
                            Aa[Ia] = oc(Aa[Ia], Ka);
                        } else {
                            ia = Jb(Ha);
                            Ka = Qa[Fb++];;
                            ja = lb();
                            ja = oc(ja, Ka);
                            rb(ja);
                        };
                        break;
                    default:
                        sc(6);
                    }
                    break kd;
                case 0xa3:
                    Ha = Qa[Fb++];;
                    Ka = Aa[(Ha >> 3) & 7];
                    if ((Ha >> 6) == 3) {
                        ja = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        ia = (ia + ((Ka >> 5) << 2)) & -1;
                        ja = fb();
                    }
                    lc(ja, Ka);
                    break kd;
                case 0xab:
                    Ha = Qa[Fb++];;
                    Ka = Aa[(Ha >> 3) & 7];
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Aa[Ia] = mc(Aa[Ia], Ka);
                    } else {
                        ia = Jb(Ha);
                        ia = (ia + ((Ka >> 5) << 2)) & -1;
                        ja = lb();
                        ja = mc(ja, Ka);
                        rb(ja);
                    }
                    break kd;
                case 0xb3:
                    Ha = Qa[Fb++];;
                    Ka = Aa[(Ha >> 3) & 7];
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Aa[Ia] = nc(Aa[Ia], Ka);
                    } else {
                        ia = Jb(Ha);
                        ia = (ia + ((Ka >> 5) << 2)) & -1;
                        ja = lb();
                        ja = nc(ja, Ka);
                        rb(ja);
                    }
                    break kd;
                case 0xbb:
                    Ha = Qa[Fb++];;
                    Ka = Aa[(Ha >> 3) & 7];
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Aa[Ia] = oc(Aa[Ia], Ka);
                    } else {
                        ia = Jb(Ha);
                        ia = (ia + ((Ka >> 5) << 2)) & -1;
                        ja = lb();
                        ja = oc(ja, Ka);
                        rb(ja);
                    }
                    break kd;
                case 0xbc:
                    Ha = Qa[Fb++];;
                    Ja = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ka = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        Ka = fb();
                    }
                    Aa[Ja] = pc(Aa[Ja], Ka);
                    break kd;
                case 0xbd:
                    Ha = Qa[Fb++];;
                    Ja = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ka = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        Ka = fb();
                    }
                    Aa[Ja] = qc(Aa[Ja], Ka);
                    break kd;
                case 0xaf:
                    Ha = Qa[Fb++];;
                    Ja = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ka = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        Ka = fb();
                    }
                    Aa[Ja] = Lc(Aa[Ja], Ka);
                    break kd;
                case 0x31:
                    if ((za.cr4 & (1 << 2)) && za.cpl != 0) sc(13);
                    ja = Uc();
                    Aa[0] = ja >>> 0;
                    Aa[2] = (ja / 0x100000000) >>> 0;
                    break kd;
                case 0xc0:
                    Ha = Qa[Fb++];;
                    Ja = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        ja = ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff);
                        Ka = Qb(0, ja, ((Aa[Ja & 3] >> ((Ja & 4) << 1)) & 0xff));
                        Ob(Ja, ja);
                        Ob(Ia, Ka);
                    } else {
                        ia = Jb(Ha);
                        ja = hb();
                        Ka = Qb(0, ja, ((Aa[Ja & 3] >> ((Ja & 4) << 1)) & 0xff));
                        nb(Ka);
                        Ob(Ja, ja);
                    }
                    break kd;
                case 0xc1:
                    Ha = Qa[Fb++];;
                    Ja = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        ja = Aa[Ia];
                        Ka = ac(0, ja, Aa[Ja]);
                        Aa[Ja] = ja;
                        Aa[Ia] = Ka;
                    } else {
                        ia = Jb(Ha);
                        ja = lb();
                        Ka = ac(0, ja, Aa[Ja]);
                        rb(Ka);
                        Aa[Ja] = ja;
                    }
                    break kd;
                case 0xb1:
                    Ha = Qa[Fb++];;
                    Ja = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        ja = Aa[Ia];
                        Ka = ac(5, Aa[0], ja);
                        if (Ka == 0) {
                            Aa[Ia] = Aa[Ja];
                        } else {
                            Aa[0] = ja;
                        }
                    } else {
                        ia = Jb(Ha);
                        ja = lb();
                        Ka = ac(5, Aa[0], ja);
                        if (Ka == 0) {
                            rb(Aa[Ja]);
                        } else {
                            Aa[0] = ja;
                        }
                    }
                    break kd;
                case 0xa0:
                    {
                        ja = za.segs[4].selector;
                        ia = (Aa[4] - 4) & -1;
                        rb(ja);
                        Aa[4] = ia;
                    };
                    break kd;
                case 0xa8:
                    {
                        ja = za.segs[5].selector;
                        ia = (Aa[4] - 4) & -1;
                        rb(ja);
                        Aa[4] = ia;
                    };
                    break kd;
                case 0xa1:
                    {
                        ia = Aa[4];
                        ja = fb();
                        he(4, ja & 0xffff);
                        Aa[4] = (Aa[4] + 4) & -1;
                    };
                    break kd;
                case 0xa9:
                    {
                        ia = Aa[4];
                        ja = fb();
                        he(5, ja & 0xffff);
                        Aa[4] = (Aa[4] + 4) & -1;
                    };
                    break kd;
                case 0xc8:
                case 0xc9:
                case 0xca:
                case 0xcb:
                case 0xcc:
                case 0xcd:
                case 0xce:
                case 0xcf:
                    Ja = b & 7;
                    ja = Aa[Ja];
                    ja = (ja >>> 24) | ((ja >> 8) & 0x0000ff00) | ((ja << 8) & 0x00ff0000) | (ja << 24);
                    Aa[Ja] = ja;
                    break kd;
                case 0x02:
                case 0x03:
                case 0x04:
                case 0x05:
                case 0x07:
                case 0x08:
                case 0x09:
                case 0x0a:
                case 0x0b:
                case 0x0c:
                case 0x0d:
                case 0x0e:
                case 0x0f:
                case 0x10:
                case 0x11:
                case 0x12:
                case 0x13:
                case 0x14:
                case 0x15:
                case 0x16:
                case 0x17:
                case 0x18:
                case 0x19:
                case 0x1a:
                case 0x1b:
                case 0x1c:
                case 0x1d:
                case 0x1e:
                case 0x1f:
                case 0x21:
                case 0x24:
                case 0x25:
                case 0x26:
                case 0x27:
                case 0x28:
                case 0x29:
                case 0x2a:
                case 0x2b:
                case 0x2c:
                case 0x2d:
                case 0x2e:
                case 0x2f:
                case 0x30:
                case 0x32:
                case 0x33:
                case 0x34:
                case 0x35:
                case 0x36:
                case 0x37:
                case 0x38:
                case 0x39:
                case 0x3a:
                case 0x3b:
                case 0x3c:
                case 0x3d:
                case 0x3e:
                case 0x3f:
                case 0x50:
                case 0x51:
                case 0x52:
                case 0x53:
                case 0x54:
                case 0x55:
                case 0x56:
                case 0x57:
                case 0x58:
                case 0x59:
                case 0x5a:
                case 0x5b:
                case 0x5c:
                case 0x5d:
                case 0x5e:
                case 0x5f:
                case 0x60:
                case 0x61:
                case 0x62:
                case 0x63:
                case 0x64:
                case 0x65:
                case 0x66:
                case 0x67:
                case 0x68:
                case 0x69:
                case 0x6a:
                case 0x6b:
                case 0x6c:
                case 0x6d:
                case 0x6e:
                case 0x6f:
                case 0x70:
                case 0x71:
                case 0x72:
                case 0x73:
                case 0x74:
                case 0x75:
                case 0x76:
                case 0x77:
                case 0x78:
                case 0x79:
                case 0x7a:
                case 0x7b:
                case 0x7c:
                case 0x7d:
                case 0x7e:
                case 0x7f:
                case 0xa6:
                case 0xa7:
                case 0xaa:
                case 0xae:
                case 0xb0:
                case 0xb8:
                case 0xb9:
                case 0xc2:
                case 0xc3:
                case 0xc4:
                case 0xc5:
                case 0xc6:
                case 0xc7:
                case 0xd0:
                case 0xd1:
                case 0xd2:
                case 0xd3:
                case 0xd4:
                case 0xd5:
                case 0xd6:
                case 0xd7:
                case 0xd8:
                case 0xd9:
                case 0xda:
                case 0xdb:
                case 0xdc:
                case 0xdd:
                case 0xde:
                case 0xdf:
                case 0xe0:
                case 0xe1:
                case 0xe2:
                case 0xe3:
                case 0xe4:
                case 0xe5:
                case 0xe6:
                case 0xe7:
                case 0xe8:
                case 0xe9:
                case 0xea:
                case 0xeb:
                case 0xec:
                case 0xed:
                case 0xee:
                case 0xef:
                case 0xf0:
                case 0xf1:
                case 0xf2:
                case 0xf3:
                case 0xf4:
                case 0xf5:
                case 0xf6:
                case 0xf7:
                case 0xf8:
                case 0xf9:
                case 0xfa:
                case 0xfb:
                case 0xfc:
                case 0xfd:
                case 0xfe:
                case 0xff:
                default:
                    sc(6);
                }
                break;
            default:
                switch (b) {
                case 0x166:
                    Ga |= 0x0100;
                    b = Qa[Fb++];;
                    b |= (Ga & 0x0100);
                    break;
                case 0x1f0:
                    Ga |= 0x0040;
                    b = Qa[Fb++];;
                    b |= (Ga & 0x0100);
                    break;
                case 0x1f2:
                    Ga |= 0x0020;
                    b = Qa[Fb++];;
                    b |= (Ga & 0x0100);
                    break;
                case 0x1f3:
                    Ga |= 0x0010;
                    b = Qa[Fb++];;
                    b |= (Ga & 0x0100);
                    break;
                case 0x164:
                    if (Ga == 0) id(Eb, b);
                    Ga = (Ga & ~0x000f) | (4 + 1);
                    b = Qa[Fb++];;
                    b |= (Ga & 0x0100);;
                    break;
                case 0x165:
                    if (Ga == 0) id(Eb, b);
                    Ga = (Ga & ~0x000f) | (5 + 1);
                    b = Qa[Fb++];;
                    b |= (Ga & 0x0100);;
                    break;
                case 0x189:
                    Ha = Qa[Fb++];;
                    ja = Aa[(Ha >> 3) & 7];
                    if ((Ha >> 6) == 3) {
                        Pb(Ha & 7, ja);
                    } else {
                        ia = Jb(Ha);
                        pb(ja);
                    }
                    break kd;
                case 0x18b:
                    Ha = Qa[Fb++];;
                    if ((Ha >> 6) == 3) {
                        ja = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        ja = db();
                    }
                    Pb((Ha >> 3) & 7, ja);
                    break kd;
                case 0x1b8:
                case 0x1b9:
                case 0x1ba:
                case 0x1bb:
                case 0x1bc:
                case 0x1bd:
                case 0x1be:
                case 0x1bf:
                    Pb(b & 7, Ib());
                    break kd;
                case 0x1a1:
                    ia = Nb();
                    ja = db();
                    Pb(0, ja);
                    break kd;
                case 0x1a3:
                    ia = Nb();
                    pb(Aa[0]);
                    break kd;
                case 0x1c7:
                    Ha = Qa[Fb++];;
                    if ((Ha >> 6) == 3) {
                        ja = Ib();
                        Pb(Ha & 7, ja);
                    } else {
                        ia = Jb(Ha);
                        ja = Ib();
                        pb(ja);
                    }
                    break kd;
                case 0x191:
                case 0x192:
                case 0x193:
                case 0x194:
                case 0x195:
                case 0x196:
                case 0x197:
                    Ja = b & 7;
                    ja = Aa[0];
                    Pb(0, Aa[Ja]);
                    Pb(Ja, ja);
                    break kd;
                case 0x187:
                    Ha = Qa[Fb++];;
                    Ja = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        ja = Aa[Ia];
                        Pb(Ia, Aa[Ja]);
                    } else {
                        ia = Jb(Ha);
                        ja = jb();
                        pb(Aa[Ja]);
                    }
                    Pb(Ja, ja);
                    break kd;
                case 0x101:
                case 0x109:
                case 0x111:
                case 0x119:
                case 0x121:
                case 0x129:
                case 0x131:
                case 0x139:
                    Ha = Qa[Fb++];;
                    Ma = (b >> 3) & 7;
                    Ka = Aa[(Ha >> 3) & 7];
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Pb(Ia, Xb(Ma, Aa[Ia], Ka));
                    } else {
                        ia = Jb(Ha);
                        if (Ma != 7) {
                            ja = jb();
                            ja = Xb(Ma, ja, Ka);
                            pb(ja);
                        } else {
                            ja = db();
                            Xb(7, ja, Ka);
                        }
                    }
                    break kd;
                case 0x103:
                case 0x10b:
                case 0x113:
                case 0x11b:
                case 0x123:
                case 0x12b:
                case 0x133:
                case 0x13b:
                    Ha = Qa[Fb++];;
                    Ma = (b >> 3) & 7;
                    Ja = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ka = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        Ka = db();
                    }
                    Pb(Ja, Xb(Ma, Aa[Ja], Ka));
                    break kd;
                case 0x105:
                case 0x10d:
                case 0x115:
                case 0x11d:
                case 0x125:
                case 0x12d:
                case 0x135:
                case 0x13d:
                    Ka = Ib();
                    Ma = (b >> 3) & 7;
                    Pb(0, Xb(Ma, Aa[0], Ka));
                    break kd;
                case 0x181:
                    Ha = Qa[Fb++];;
                    Ma = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Ka = Ib();
                        Aa[Ia] = Xb(Ma, Aa[Ia], Ka);
                    } else {
                        ia = Jb(Ha);
                        Ka = Ib();
                        if (Ma != 7) {
                            ja = jb();
                            ja = Xb(Ma, ja, Ka);
                            pb(ja);
                        } else {
                            ja = db();
                            Xb(7, ja, Ka);
                        }
                    }
                    break kd;
                case 0x183:
                    Ha = Qa[Fb++];;
                    Ma = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Ka = ((Qa[Fb++] << 24) >> 24);;
                        Pb(Ia, Xb(Ma, Aa[Ia], Ka));
                    } else {
                        ia = Jb(Ha);
                        Ka = ((Qa[Fb++] << 24) >> 24);;
                        if (Ma != 7) {
                            ja = jb();
                            ja = Xb(Ma, ja, Ka);
                            pb(ja);
                        } else {
                            ja = db();
                            Xb(7, ja, Ka);
                        }
                    }
                    break kd;
                case 0x140:
                case 0x141:
                case 0x142:
                case 0x143:
                case 0x144:
                case 0x145:
                case 0x146:
                case 0x147:
                    Ja = b & 7;
                    Pb(Ja, Yb(Aa[Ja]));
                    break kd;
                case 0x148:
                case 0x149:
                case 0x14a:
                case 0x14b:
                case 0x14c:
                case 0x14d:
                case 0x14e:
                case 0x14f:
                    Ja = b & 7;
                    Pb(Ja, Zb(Aa[Ja]));
                    break kd;
                case 0x16b:
                    Ha = Qa[Fb++];;
                    Ja = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ka = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        Ka = db();
                    }
                    La = ((Qa[Fb++] << 24) >> 24);;
                    Pb(Ja, Gc(Ka, La));
                    break kd;
                case 0x169:
                    Ha = Qa[Fb++];;
                    Ja = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ka = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        Ka = db();
                    }
                    La = Ib();
                    Pb(Ja, Gc(Ka, La));
                    break kd;
                case 0x185:
                    Ha = Qa[Fb++];;
                    if ((Ha >> 6) == 3) {
                        ja = Aa[Ha & 7];
                    } else {
                        ia = Jb(Ha);
                        ja = db();
                    }
                    Ka = Aa[(Ha >> 3) & 7];
                    Ca = ja & Ka;
                    Da = 13;
                    break kd;
                case 0x1a9:
                    Ka = Ib();
                    Ca = Aa[0] & Ka;
                    Da = 13;
                    break kd;
                case 0x1f7:
                    Ha = Qa[Fb++];;
                    Ma = (Ha >> 3) & 7;
                    switch (Ma) {
                    case 0:
                        if ((Ha >> 6) == 3) {
                            ja = Aa[Ha & 7];
                        } else {
                            ia = Jb(Ha);
                            ja = db();
                        }
                        Ka = Ib();
                        Ca = ja & Ka;
                        Da = 13;
                        break;
                    case 2:
                        if ((Ha >> 6) == 3) {
                            Ia = Ha & 7;
                            Pb(Ia, ~Aa[Ia]);
                        } else {
                            ia = Jb(Ha);
                            ja = jb();
                            ja = ~ja;
                            pb(ja);
                        }
                        break;
                    case 3:
                        if ((Ha >> 6) == 3) {
                            Ia = Ha & 7;
                            Pb(Ia, Xb(5, 0, Aa[Ia]));
                        } else {
                            ia = Jb(Ha);
                            ja = jb();
                            ja = Xb(5, 0, ja);
                            pb(ja);
                        }
                        break;
                    case 4:
                        if ((Ha >> 6) == 3) {
                            ja = Aa[Ha & 7];
                        } else {
                            ia = Jb(Ha);
                            ja = db();
                        }
                        ja = Fc(Aa[0], ja);
                        Pb(0, ja);
                        Pb(2, ja >> 16);
                        break;
                    case 5:
                        if ((Ha >> 6) == 3) {
                            ja = Aa[Ha & 7];
                        } else {
                            ia = Jb(Ha);
                            ja = db();
                        }
                        ja = Gc(Aa[0], ja);
                        Pb(0, ja);
                        Pb(2, ja >> 16);
                        break;
                    case 6:
                        if ((Ha >> 6) == 3) {
                            ja = Aa[Ha & 7];
                        } else {
                            ia = Jb(Ha);
                            ja = db();
                        }
                        uc(ja);
                        break;
                    case 7:
                        if ((Ha >> 6) == 3) {
                            ja = Aa[Ha & 7];
                        } else {
                            ia = Jb(Ha);
                            ja = db();
                        }
                        vc(ja);
                        break;
                    default:
                        sc(6);
                    }
                    break kd;
                case 0x1c1:
                    Ha = Qa[Fb++];;
                    Ma = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ka = Qa[Fb++];;
                        Ia = Ha & 7;
                        Pb(Ia, gc(Ma, Aa[Ia], Ka));
                    } else {
                        ia = Jb(Ha);
                        Ka = Qa[Fb++];;
                        ja = jb();
                        ja = gc(Ma, ja, Ka);
                        pb(ja);
                    }
                    break kd;
                case 0x1d1:
                    Ha = Qa[Fb++];;
                    Ma = (Ha >> 3) & 7;
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Pb(Ia, gc(Ma, Aa[Ia], 1));
                    } else {
                        ia = Jb(Ha);
                        ja = jb();
                        ja = gc(Ma, ja, 1);
                        pb(ja);
                    }
                    break kd;
                case 0x1d3:
                    Ha = Qa[Fb++];;
                    Ma = (Ha >> 3) & 7;
                    Ka = Aa[1] & 0xff;
                    if ((Ha >> 6) == 3) {
                        Ia = Ha & 7;
                        Pb(Ia, gc(Ma, Aa[Ia], Ka));
                    } else {
                        ia = Jb(Ha);
                        ja = jb();
                        ja = gc(Ma, ja, Ka);
                        pb(ja);
                    }
                    break kd;
                case 0x198:
                    Pb(0, (Aa[0] << 24) >> 24);
                    break kd;
                case 0x199:
                    Pb(2, (Aa[0] << 16) >> 31);
                    break kd;
                case 0x1ff:
                    Ha = Qa[Fb++];;
                    Ma = (Ha >> 3) & 7;
                    switch (Ma) {
                    case 0:
                        if ((Ha >> 6) == 3) {
                            Ia = Ha & 7;
                            Pb(Ia, Yb(Aa[Ia]));
                        } else {
                            ia = Jb(Ha);
                            ja = jb();
                            ja = Yb(ja);
                            pb(ja);
                        }
                        break;
                    case 1:
                        if ((Ha >> 6) == 3) {
                            Ia = Ha & 7;
                            Pb(Ia, Zb(Aa[Ia]));
                        } else {
                            ia = Jb(Ha);
                            ja = jb();
                            ja = Zb(ja);
                            pb(ja);
                        }
                        break;
                    case 2:
                    case 4:
                    case 6:
                    case 3:
                    case 5:
                    default:
                        throw "GRP5";
                    }
                    break kd;
                case 0x190:
                    break kd;
                case 0x1a5:
                    if (Ga & (0x0010 | 0x0020)) {
                        if (Aa[1]) {
                            if (16 === 32 && (Aa[1] >>> 0) >= 4 && za.df == 1 && ((Aa[6] | Aa[7]) & 3) == 0 && cd()) {} else {
                                ia = Aa[6];
                                ja = db();
                                ia = Aa[7];
                                pb(ja);
                                Aa[6] = (Aa[6] + (za.df << 1)) & -1;
                                Aa[7] = (Aa[7] + (za.df << 1)) & -1;
                                Aa[1] = (Aa[1] - 1) & -1;
                            }
                            Fb = Hb;
                        }
                    } else {
                        ia = Aa[6];
                        ja = db();
                        ia = Aa[7];
                        pb(ja);
                        Aa[6] = (Aa[6] + (za.df << 1)) & -1;
                        Aa[7] = (Aa[7] + (za.df << 1)) & -1;
                    };
                    break kd;
                case 0x1a7:
                    if (Ga & (0x0010 | 0x0020)) {
                        if (Aa[1]) {
                            ia = Aa[6];
                            ja = db();
                            ia = Aa[7];
                            Ka = db();
                            Xb(7, ja, Ka);
                            Aa[6] = (Aa[6] + (za.df << 1)) & -1;
                            Aa[7] = (Aa[7] + (za.df << 1)) & -1;
                            Aa[1] = (Aa[1] - 1) & -1;
                            if (Ga & 0x0010) {
                                if (!Ub(4)) break kd;
                            } else {
                                if (Ub(4)) break kd;
                            }
                            Fb = Hb;
                        }
                    } else {
                        ia = Aa[6];
                        ja = db();
                        ia = Aa[7];
                        Ka = db();
                        Xb(7, ja, Ka);
                        Aa[6] = (Aa[6] + (za.df << 1)) & -1;
                        Aa[7] = (Aa[7] + (za.df << 1)) & -1;
                    };
                    break kd;
                case 0x1ad:
                    if (Ga & (0x0010 | 0x0020)) {
                        if (Aa[1]) {
                            ia = Aa[6];
                            if (16 == 32) Aa[0] = fb();
                            else Pb(0, db());
                            Aa[6] = (ia + (za.df << 1)) & -1;
                            Aa[1] = (Aa[1] - 1) & -1;
                            Fb = Hb;
                        }
                    } else {
                        ia = Aa[6];
                        if (16 == 32) Aa[0] = fb();
                        else Pb(0, db());
                        Aa[6] = (ia + (za.df << 1)) & -1;
                    };
                    break kd;
                case 0x1af:
                    if (Ga & (0x0010 | 0x0020)) {
                        if (Aa[1]) {
                            ia = Aa[7];
                            ja = db();
                            Xb(7, Aa[0], ja);
                            Aa[7] = (Aa[7] + (za.df << 1)) & -1;
                            Aa[1] = (Aa[1] - 1) & -1;
                            if (Ga & 0x0010) {
                                if (!Ub(4)) break kd;
                            } else {
                                if (Ub(4)) break kd;
                            }
                            Fb = Hb;
                        }
                    } else {
                        ia = Aa[7];
                        ja = db();
                        Xb(7, Aa[0], ja);
                        Aa[7] = (Aa[7] + (za.df << 1)) & -1;
                    };
                    break kd;
                case 0x1ab:
                    if (Ga & (0x0010 | 0x0020)) {
                        if (Aa[1]) {
                            if (16 === 32 && (Aa[1] >>> 0) >= 4 && za.df == 1 && (Aa[7] & 3) == 0 && hd()) {} else {
                                ia = Aa[7];
                                pb(Aa[0]);
                                Aa[7] = (ia + (za.df << 1)) & -1;
                                Aa[1] = (Aa[1] - 1) & -1;
                            }
                            Fb = Hb;
                        }
                    } else {
                        ia = Aa[7];
                        pb(Aa[0]);
                        Aa[7] = (ia + (za.df << 1)) & -1;
                    };
                    break kd;
                case 0x1d8:
                case 0x1d9:
                case 0x1da:
                case 0x1db:
                case 0x1dc:
                case 0x1dd:
                case 0x1de:
                case 0x1df:
                    b &= 0xff;
                    break;
                case 0x1e5:
                    ze = (za.eflags >> 12) & 3;
                    if (za.cpl > ze) sc(13);
                    ja = Qa[Fb++];;
                    Pb(0, za.ld16_port(ja)); {
                        if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                    };
                    break kd;
                case 0x1e7:
                    ze = (za.eflags >> 12) & 3;
                    if (za.cpl > ze) sc(13);
                    ja = Qa[Fb++];;
                    za.st16_port(ja, Aa[0] & 0xffff); {
                        if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                    };
                    break kd;
                case 0x1ed:
                    ze = (za.eflags >> 12) & 3;
                    if (za.cpl > ze) sc(13);
                    Pb(0, za.ld16_port(Aa[2] & 0xffff)); {
                        if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                    };
                    break kd;
                case 0x1ef:
                    ze = (za.eflags >> 12) & 3;
                    if (za.cpl > ze) sc(13);
                    za.st16_port(Aa[2] & 0xffff, Aa[0] & 0xffff); {
                        if (za.hard_irq != 0 && (za.eflags & 0x00000200)) break Se;
                    };
                    break kd;
                case 0x162:
                    Re();
                    break kd;
                case 0x100:
                case 0x102:
                case 0x104:
                case 0x106:
                case 0x107:
                case 0x108:
                case 0x10a:
                case 0x10c:
                case 0x10e:
                case 0x110:
                case 0x112:
                case 0x114:
                case 0x116:
                case 0x117:
                case 0x118:
                case 0x11a:
                case 0x11c:
                case 0x11e:
                case 0x11f:
                case 0x120:
                case 0x122:
                case 0x124:
                case 0x126:
                case 0x127:
                case 0x128:
                case 0x12a:
                case 0x12c:
                case 0x12e:
                case 0x12f:
                case 0x130:
                case 0x132:
                case 0x134:
                case 0x136:
                case 0x137:
                case 0x138:
                case 0x13a:
                case 0x13c:
                case 0x13e:
                case 0x13f:
                case 0x150:
                case 0x151:
                case 0x152:
                case 0x153:
                case 0x154:
                case 0x155:
                case 0x156:
                case 0x157:
                case 0x158:
                case 0x159:
                case 0x15a:
                case 0x15b:
                case 0x15c:
                case 0x15d:
                case 0x15e:
                case 0x15f:
                case 0x160:
                case 0x161:
                case 0x163:
                case 0x167:
                case 0x168:
                case 0x16a:
                case 0x16c:
                case 0x16d:
                case 0x16e:
                case 0x16f:
                case 0x170:
                case 0x171:
                case 0x172:
                case 0x173:
                case 0x174:
                case 0x175:
                case 0x176:
                case 0x177:
                case 0x178:
                case 0x179:
                case 0x17a:
                case 0x17b:
                case 0x17c:
                case 0x17d:
                case 0x17e:
                case 0x17f:
                case 0x180:
                case 0x182:
                case 0x184:
                case 0x186:
                case 0x188:
                case 0x18a:
                case 0x18c:
                case 0x18d:
                case 0x18e:
                case 0x18f:
                case 0x19a:
                case 0x19b:
                case 0x19c:
                case 0x19d:
                case 0x19e:
                case 0x19f:
                case 0x1a0:
                case 0x1a2:
                case 0x1a4:
                case 0x1a6:
                case 0x1a8:
                case 0x1aa:
                case 0x1ac:
                case 0x1ae:
                case 0x1b0:
                case 0x1b1:
                case 0x1b2:
                case 0x1b3:
                case 0x1b4:
                case 0x1b5:
                case 0x1b6:
                case 0x1b7:
                case 0x1c0:
                case 0x1c2:
                case 0x1c3:
                case 0x1c4:
                case 0x1c5:
                case 0x1c6:
                case 0x1c8:
                case 0x1c9:
                case 0x1ca:
                case 0x1cb:
                case 0x1cc:
                case 0x1cd:
                case 0x1ce:
                case 0x1cf:
                case 0x1d0:
                case 0x1d2:
                case 0x1d4:
                case 0x1d5:
                case 0x1d6:
                case 0x1d7:
                case 0x1e0:
                case 0x1e1:
                case 0x1e2:
                case 0x1e3:
                case 0x1e4:
                case 0x1e6:
                case 0x1e8:
                case 0x1e9:
                case 0x1ea:
                case 0x1eb:
                case 0x1ec:
                case 0x1ee:
                case 0x1f1:
                case 0x1f4:
                case 0x1f5:
                case 0x1f6:
                case 0x1f8:
                case 0x1f9:
                case 0x1fa:
                case 0x1fb:
                case 0x1fc:
                case 0x1fd:
                case 0x1fe:
                default:
                    sc(6);
                case 0x10f:
                    b = Qa[Fb++];;
                    b |= 0x0100;
                    switch (b) {
                    case 0x140:
                    case 0x141:
                    case 0x142:
                    case 0x143:
                    case 0x144:
                    case 0x145:
                    case 0x146:
                    case 0x147:
                    case 0x148:
                    case 0x149:
                    case 0x14a:
                    case 0x14b:
                    case 0x14c:
                    case 0x14d:
                    case 0x14e:
                    case 0x14f:
                        Ha = Qa[Fb++];;
                        if ((Ha >> 6) == 3) {
                            ja = Aa[Ha & 7];
                        } else {
                            ia = Jb(Ha);
                            ja = db();
                        }
                        if (Ub(b & 0xf)) Pb((Ha >> 3) & 7, ja);
                        break kd;
                    case 0x1b6:
                        Ha = Qa[Fb++];;
                        Ja = (Ha >> 3) & 7;
                        if ((Ha >> 6) == 3) {
                            Ia = Ha & 7;
                            ja = ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff);
                        } else {
                            ia = Jb(Ha);
                            ja = bb();
                        }
                        Pb(Ja, ja);
                        break kd;
                    case 0x1be:
                        Ha = Qa[Fb++];;
                        Ja = (Ha >> 3) & 7;
                        if ((Ha >> 6) == 3) {
                            Ia = Ha & 7;
                            ja = ((Aa[Ia & 3] >> ((Ia & 4) << 1)) & 0xff);
                        } else {
                            ia = Jb(Ha);
                            ja = bb();
                        }
                        Pb(Ja, (ja << 24) >> 24);
                        break kd;
                    case 0x1af:
                        Ha = Qa[Fb++];;
                        Ja = (Ha >> 3) & 7;
                        if ((Ha >> 6) == 3) {
                            Ka = Aa[Ha & 7];
                        } else {
                            ia = Jb(Ha);
                            Ka = db();
                        }
                        Pb(Ja, Gc(Aa[Ja], Ka));
                        break kd;
                    case 0x1c1:
                        Ha = Qa[Fb++];;
                        Ja = (Ha >> 3) & 7;
                        if ((Ha >> 6) == 3) {
                            Ia = Ha & 7;
                            ja = Aa[Ia];
                            Ka = Xb(0, ja, Aa[Ja]);
                            Pb(Ja, ja);
                            Pb(Ia, Ka);
                        } else {
                            ia = Jb(Ha);
                            ja = jb();
                            Ka = Xb(0, ja, Aa[Ja]);
                            pb(Ka);
                            Pb(Ja, ja);
                        }
                        break kd;
                    case 0x100:
                    case 0x101:
                    case 0x102:
                    case 0x103:
                    case 0x104:
                    case 0x105:
                    case 0x106:
                    case 0x107:
                    case 0x108:
                    case 0x109:
                    case 0x10a:
                    case 0x10b:
                    case 0x10c:
                    case 0x10d:
                    case 0x10e:
                    case 0x10f:
                    case 0x110:
                    case 0x111:
                    case 0x112:
                    case 0x113:
                    case 0x114:
                    case 0x115:
                    case 0x116:
                    case 0x117:
                    case 0x118:
                    case 0x119:
                    case 0x11a:
                    case 0x11b:
                    case 0x11c:
                    case 0x11d:
                    case 0x11e:
                    case 0x11f:
                    case 0x120:
                    case 0x121:
                    case 0x122:
                    case 0x123:
                    case 0x124:
                    case 0x125:
                    case 0x126:
                    case 0x127:
                    case 0x128:
                    case 0x129:
                    case 0x12a:
                    case 0x12b:
                    case 0x12c:
                    case 0x12d:
                    case 0x12e:
                    case 0x12f:
                    case 0x130:
                    case 0x131:
                    case 0x132:
                    case 0x133:
                    case 0x134:
                    case 0x135:
                    case 0x136:
                    case 0x137:
                    case 0x138:
                    case 0x139:
                    case 0x13a:
                    case 0x13b:
                    case 0x13c:
                    case 0x13d:
                    case 0x13e:
                    case 0x13f:
                    case 0x150:
                    case 0x151:
                    case 0x152:
                    case 0x153:
                    case 0x154:
                    case 0x155:
                    case 0x156:
                    case 0x157:
                    case 0x158:
                    case 0x159:
                    case 0x15a:
                    case 0x15b:
                    case 0x15c:
                    case 0x15d:
                    case 0x15e:
                    case 0x15f:
                    case 0x160:
                    case 0x161:
                    case 0x162:
                    case 0x163:
                    case 0x164:
                    case 0x165:
                    case 0x166:
                    case 0x167:
                    case 0x168:
                    case 0x169:
                    case 0x16a:
                    case 0x16b:
                    case 0x16c:
                    case 0x16d:
                    case 0x16e:
                    case 0x16f:
                    case 0x170:
                    case 0x171:
                    case 0x172:
                    case 0x173:
                    case 0x174:
                    case 0x175:
                    case 0x176:
                    case 0x177:
                    case 0x178:
                    case 0x179:
                    case 0x17a:
                    case 0x17b:
                    case 0x17c:
                    case 0x17d:
                    case 0x17e:
                    case 0x17f:
                    case 0x180:
                    case 0x181:
                    case 0x182:
                    case 0x183:
                    case 0x184:
                    case 0x185:
                    case 0x186:
                    case 0x187:
                    case 0x188:
                    case 0x189:
                    case 0x18a:
                    case 0x18b:
                    case 0x18c:
                    case 0x18d:
                    case 0x18e:
                    case 0x18f:
                    case 0x190:
                    case 0x191:
                    case 0x192:
                    case 0x193:
                    case 0x194:
                    case 0x195:
                    case 0x196:
                    case 0x197:
                    case 0x198:
                    case 0x199:
                    case 0x19a:
                    case 0x19b:
                    case 0x19c:
                    case 0x19d:
                    case 0x19e:
                    case 0x19f:
                    case 0x1a0:
                    case 0x1a1:
                    case 0x1a2:
                    case 0x1a3:
                    case 0x1a4:
                    case 0x1a5:
                    case 0x1a6:
                    case 0x1a7:
                    case 0x1a8:
                    case 0x1a9:
                    case 0x1aa:
                    case 0x1ab:
                    case 0x1ac:
                    case 0x1ad:
                    case 0x1ae:
                    case 0x1b0:
                    case 0x1b1:
                    case 0x1b2:
                    case 0x1b3:
                    case 0x1b4:
                    case 0x1b5:
                    case 0x1b7:
                    case 0x1b8:
                    case 0x1b9:
                    case 0x1ba:
                    case 0x1bb:
                    case 0x1bc:
                    case 0x1bd:
                    case 0x1bf:
                    case 0x1c0:
                    default:
                        sc(6);
                    }
                    break;
                }
            }
        }
    }
    while (--Na);
    this.cycle_count += (xa - Na);
    this.eip = (Eb + Fb - Hb);
    this.cc_src = Ba;
    this.cc_dst = Ca;
    this.cc_op = Da;
    this.cc_op2 = Ea;
    this.cc_dst2 = Fa;
    return Oa;
};
CPU_X86.prototype.exec = function (xa) {
    var Ve, Oa, We, ya;
    We = this.cycle_count + xa;
    Oa = 256;
    ya = null;
    while (this.cycle_count < We) {
        try {
            Oa = this.exec_internal(We - this.cycle_count, ya);
            if (Oa != 256) break;
            ya = null;
        } catch (Xe) {
            if (Xe.hasOwnProperty("intno")) {
                ya = Xe;
            } else {
                throw Xe;
            }
        }
    }
    return Oa;
};
CPU_X86.prototype.load_binary_ie9 = function (Ye, ia) {
    var Ze, af, dd, i;
    Ze = new XMLHttpRequest();
    Ze.open('GET', Ye, false);
    Ze.send(null);
    if (Ze.status != 200 && Ze.status != 0) {
        throw "Error while loading " + Ye;
    }
    af = new VBArray(Ze.responseBody).toArray();
    dd = af.length;
    for (i = 0; i < dd; i++) {
        this.st8_phys(ia + i, af[i]);
    }
    return dd;
};
CPU_X86.prototype.load_binary = function (Ye, ia) {
    var Ze, af, dd, i, bf, cf;
    if (typeof ActiveXObject == "function") return this.load_binary_ie9(Ye, ia);
    Ze = new XMLHttpRequest();
    Ze.open('GET', Ye, false);
    if ('mozResponseType' in Ze) {
        Ze.mozResponseType = 'arraybuffer';
    } else if ('responseType' in Ze) {
        Ze.responseType = 'arraybuffer';
    } else {
        Ze.overrideMimeType('text/plain; charset=x-user-defined');
    }
    Ze.send(null);
    if (Ze.status != 200 && Ze.status != 0) {
        throw "Error while loading " + Ye;
    }
    cf = true;
    if ('mozResponse' in Ze) {
        af = Ze.mozResponse;
    } else if (Ze.mozResponseArrayBuffer) {
        af = Ze.mozResponseArrayBuffer;
    } else if ('responseType' in Ze) {
        af = Ze.response;
    } else {
        af = Ze.responseText;
        cf = false;
    }
    if (cf) {
        dd = af.byteLength;
        bf = new Uint8Array(af, 0, dd);
        for (i = 0; i < dd; i++) {
            this.st8_phys(ia + i, bf[i]);
        }
    } else {
        dd = af.length;
        for (i = 0; i < dd; i++) {
            this.st8_phys(ia + i, af.charCodeAt(i));
        }
    }
    return dd;
};

function df(a) {
    return ((a / 10) << 4) | (a % 10);
}
function ef(ff) {
    var gf, d;
    var i;
    gf = new Array();
    for (i = 0; i < 128; i++) gf[i] = 0;
    this.cmos_data = gf;
    this.cmos_index = 0;
    d = new Date();
    gf[0] = df(d.getUTCSeconds());
    gf[2] = df(d.getUTCMinutes());
    gf[4] = df(d.getUTCHours());
    gf[6] = df(d.getUTCDay());
    gf[7] = df(d.getUTCDate());
    gf[8] = df(d.getUTCMonth() + 1);
    gf[9] = df(d.getUTCFullYear() % 100);
    gf[10] = 0x26;
    gf[11] = 0x02;
    gf[12] = 0x00;
    gf[13] = 0x80;
    gf[0x14] = 0x02;
    ff.register_ioport_write(0x70, 2, 1, this.ioport_write.bind(this));
    ff.register_ioport_read(0x70, 2, 1, this.ioport_read.bind(this));
}
ef.prototype.ioport_write = function (ia, af) {
    if (ia == 0x70) {
        this.cmos_index = af & 0x7f;
    }
};
ef.prototype.ioport_read = function (ia) {
    var hf;
    if (ia == 0x70) {
        return 0xff;
    } else {
        hf = this.cmos_data[this.cmos_index];
        if (this.cmos_index == 10) this.cmos_data[10] ^= 0x80;
        else if (this.cmos_index == 12) this.cmos_data[12] = 0x00;
        return hf;
    }
};

function jf(ff, kf) {
    ff.register_ioport_write(kf, 2, 1, this.ioport_write.bind(this));
    ff.register_ioport_read(kf, 2, 1, this.ioport_read.bind(this));
    this.reset();
}
jf.prototype.reset = function () {
    this.last_irr = 0;
    this.irr = 0;
    this.imr = 0;
    this.isr = 0;
    this.priority_add = 0;
    this.irq_base = 0;
    this.read_reg_select = 0;
    this.special_mask = 0;
    this.init_state = 0;
    this.auto_eoi = 0;
    this.rotate_on_autoeoi = 0;
    this.init4 = 0;
    this.elcr = 0;
    this.elcr_mask = 0;
};
jf.prototype.set_irq1 = function (lf, mf) {
    var nf;
    nf = 1 << lf;
    if (mf) {
        if ((this.last_irr & nf) == 0) this.irr |= nf;
        this.last_irr |= nf;
    } else {
        this.last_irr &= ~nf;
    }
};
jf.prototype.get_priority = function (nf) {
    var of;
    if (nf == 0) return -1;
    of = 7;
    while ((nf & (1 << ((of + this.priority_add) & 7))) == 0) of--;
    return of;
};
jf.prototype.get_irq = function () {
    var nf, pf, of;
    nf = this.irr & ~this.imr;
    of = this.get_priority(nf);
    if (of < 0) return -1;
    pf = this.get_priority(this.isr);
    if (of > pf) {
        return of;
    } else {
        return -1;
    }
};
jf.prototype.intack = function (lf) {
    if (this.auto_eoi) {
        if (this.rotate_on_auto_eoi) this.priority_add = (lf + 1) & 7;
    } else {
        this.isr |= (1 << lf);
    }
    if (!(this.elcr & (1 << lf))) this.irr &= ~ (1 << lf);
};
jf.prototype.ioport_write = function (ia, ja) {
    var of;
    ia &= 1;
    if (ia == 0) {
        if (ja & 0x10) {
            this.reset();
            this.init_state = 1;
            this.init4 = ja & 1;
            if (ja & 0x02) throw "single mode not supported";
            if (ja & 0x08) throw "level sensitive irq not supported";
        } else if (ja & 0x08) {
            if (ja & 0x02) this.read_reg_select = ja & 1;
            if (ja & 0x40) this.special_mask = (ja >> 5) & 1;
        } else {
            switch (ja) {
            case 0x00:
            case 0x80:
                this.rotate_on_autoeoi = ja >> 7;
                break;
            case 0x20:
            case 0xa0:
                of = this.get_priority(this.isr);
                if (of >= 0) {
                    this.isr &= ~ (1 << ((of + this.priority_add) & 7));
                }
                if (ja == 0xa0) this.priority_add = (this.priority_add + 1) & 7;
                break;
            case 0x60:
            case 0x61:
            case 0x62:
            case 0x63:
            case 0x64:
            case 0x65:
            case 0x66:
            case 0x67:
                of = ja & 7;
                this.isr &= ~ (1 << of);
                break;
            case 0xc0:
            case 0xc1:
            case 0xc2:
            case 0xc3:
            case 0xc4:
            case 0xc5:
            case 0xc6:
            case 0xc7:
                this.priority_add = (ja + 1) & 7;
                break;
            case 0xe0:
            case 0xe1:
            case 0xe2:
            case 0xe3:
            case 0xe4:
            case 0xe5:
            case 0xe6:
            case 0xe7:
                of = ja & 7;
                this.isr &= ~ (1 << of);
                this.priority_add = (of + 1) & 7;
                break;
            }
        }
    } else {
        switch (this.init_state) {
        case 0:
            this.imr = ja;
            this.update_irq();
            break;
        case 1:
            this.irq_base = ja & 0xf8;
            this.init_state = 2;
            break;
        case 2:
            if (this.init4) {
                this.init_state = 3;
            } else {
                this.init_state = 0;
            }
            break;
        case 3:
            this.auto_eoi = (ja >> 1) & 1;
            this.init_state = 0;
            break;
        }
    }
};
jf.prototype.ioport_read = function (qf) {
    var ia, hf;
    ia = qf & 1;
    if (ia == 0) {
        if (this.read_reg_select) hf = this.isr;
        else hf = this.irr;
    } else {
        hf = this.imr;
    }
    return hf;
};

function rf(ff, sf, qf, tf) {
    this.pics = new Array();
    this.pics[0] = new jf(ff, sf);
    this.pics[1] = new jf(ff, qf);
    this.pics[0].elcr_mask = 0xf8;
    this.pics[1].elcr_mask = 0xde;
    this.irq_requested = 0;
    this.cpu_set_irq = tf;
    this.pics[0].update_irq = this.update_irq.bind(this);
    this.pics[1].update_irq = this.update_irq.bind(this);
}
rf.prototype.update_irq = function () {
    var uf, lf;
    uf = this.pics[1].get_irq();
    if (uf >= 0) {
        this.pics[0].set_irq1(2, 1);
        this.pics[0].set_irq1(2, 0);
    }
    lf = this.pics[0].get_irq();
    if (lf >= 0) {
        this.cpu_set_irq(1);
    } else {
        this.cpu_set_irq(0);
    }
};
rf.prototype.set_irq = function (lf, mf) {
    this.pics[lf >> 3].set_irq1(lf & 7, mf);
    this.update_irq();
};
rf.prototype.get_hard_intno = function () {
    var lf, uf, intno;
    lf = this.pics[0].get_irq();
    if (lf >= 0) {
        this.pics[0].intack(lf);
        if (lf == 2) {
            uf = this.pics[1].get_irq();
            if (uf >= 0) {
                this.pics[1].intack(uf);
            } else {
                uf = 7;
            }
            intno = this.pics[1].irq_base + uf;
            lf = uf + 8;
        } else {
            intno = this.pics[0].irq_base + lf;
        }
    } else {
        lf = 7;
        intno = this.pics[0].irq_base + lf;
    }
    this.update_irq();
    return intno;
};

function vf(ff, wf, xf) {
    var s, i;
    this.pit_channels = new Array();
    for (i = 0; i < 3; i++) {
        s = new yf(xf);
        this.pit_channels[i] = s;
        s.mode = 3;
        s.gate = (i != 2) >> 0;
        s.pit_load_count(0);
    }
    this.speaker_data_on = 0;
    this.set_irq = wf;
    ff.register_ioport_write(0x40, 4, 1, this.ioport_write.bind(this));
    ff.register_ioport_read(0x40, 3, 1, this.ioport_read.bind(this));
    ff.register_ioport_read(0x61, 1, 1, this.speaker_ioport_read.bind(this));
    ff.register_ioport_write(0x61, 1, 1, this.speaker_ioport_write.bind(this));
}
function yf(xf) {
    this.count = 0;
    this.latched_count = 0;
    this.rw_state = 0;
    this.mode = 0;
    this.bcd = 0;
    this.gate = 0;
    this.count_load_time = 0;
    this.get_ticks = xf;
    this.pit_time_unit = 1193182 / 2000000;
}
yf.prototype.get_time = function () {
    return Math.floor(this.get_ticks() * this.pit_time_unit);
};
yf.prototype.pit_get_count = function () {
    var d, zf;
    d = this.get_time() - this.count_load_time;
    switch (this.mode) {
    case 0:
    case 1:
    case 4:
    case 5:
        zf = (this.count - d) & 0xffff;
        break;
    default:
        zf = this.count - (d % this.count);
        break;
    }
    return zf;
};
yf.prototype.pit_get_out = function () {
    var d, Af;
    d = this.get_time() - this.count_load_time;
    switch (this.mode) {
    default:
    case 0:
        Af = (d >= this.count) >> 0;
        break;
    case 1:
        Af = (d < this.count) >> 0;
        break;
    case 2:
        if ((d % this.count) == 0 && d != 0) Af = 1;
        else Af = 0;
        break;
    case 3:
        Af = ((d % this.count) < (this.count >> 1)) >> 0;
        break;
    case 4:
    case 5:
        Af = (d == this.count) >> 0;
        break;
    }
    return Af;
};
yf.prototype.get_next_transition_time = function () {
    var d, Bf, base, Cf;
    d = this.get_time() - this.count_load_time;
    switch (this.mode) {
    default:
    case 0:
    case 1:
        if (d < this.count) Bf = this.count;
        else return -1;
        break;
    case 2:
        base = (d / this.count) * this.count;
        if ((d - base) == 0 && d != 0) Bf = base + this.count;
        else Bf = base + this.count + 1;
        break;
    case 3:
        base = (d / this.count) * this.count;
        Cf = ((this.count + 1) >> 1);
        if ((d - base) < Cf) Bf = base + Cf;
        else Bf = base + this.count;
        break;
    case 4:
    case 5:
        if (d < this.count) Bf = this.count;
        else if (d == this.count) Bf = this.count + 1;
        else return -1;
        break;
    }
    Bf = this.count_load_time + Bf;
    return Bf;
};
yf.prototype.pit_load_count = function (ja) {
    if (ja == 0) ja = 0x10000;
    this.count_load_time = this.get_time();
    this.count = ja;
};
vf.prototype.ioport_write = function (ia, ja) {
    var Df, Ef, s;
    ia &= 3;
    if (ia == 3) {
        Df = ja >> 6;
        if (Df == 3) return;
        s = this.pit_channels[Df];
        Ef = (ja >> 4) & 3;
        switch (Ef) {
        case 0:
            s.latched_count = s.pit_get_count();
            s.rw_state = 4;
            break;
        default:
            s.mode = (ja >> 1) & 7;
            s.bcd = ja & 1;
            s.rw_state = Ef - 1 + 0;
            break;
        }
    } else {
        s = this.pit_channels[ia];
        switch (s.rw_state) {
        case 0:
            s.pit_load_count(ja);
            break;
        case 1:
            s.pit_load_count(ja << 8);
            break;
        case 2:
        case 3:
            if (s.rw_state & 1) {
                s.pit_load_count((s.latched_count & 0xff) | (ja << 8));
            } else {
                s.latched_count = ja;
            }
            s.rw_state ^= 1;
            break;
        }
    }
};
vf.prototype.ioport_read = function (ia) {
    var hf, pa, s;
    ia &= 3;
    s = this.pit_channels[ia];
    switch (s.rw_state) {
    case 0:
    case 1:
    case 2:
    case 3:
        pa = s.pit_get_count();
        if (s.rw_state & 1) hf = (pa >> 8) & 0xff;
        else hf = pa & 0xff;
        if (s.rw_state & 2) s.rw_state ^= 1;
        break;
    default:
    case 4:
    case 5:
        if (s.rw_state & 1) hf = s.latched_count >> 8;
        else hf = s.latched_count & 0xff;
        s.rw_state ^= 1;
        break;
    }
    return hf;
};
vf.prototype.speaker_ioport_write = function (ia, ja) {
    this.speaker_data_on = (ja >> 1) & 1;
    this.pit_channels[2].gate = ja & 1;
};
vf.prototype.speaker_ioport_read = function (ia) {
    var Af, s, ja;
    s = this.pit_channels[2];
    Af = s.pit_get_out();
    ja = (this.speaker_data_on << 1) | s.gate | (Af << 5);
    return ja;
};
vf.prototype.update_irq = function () {
    this.set_irq(1);
    this.set_irq(0);
};

function Ff(ff, ia, Gf, Hf) {
    this.divider = 0;
    this.rbr = 0;
    this.ier = 0;
    this.iir = 0x01;
    this.lcr = 0;
    this.mcr;
    this.lsr = 0x40 | 0x20;
    this.msr = 0;
    this.scr = 0;
    this.set_irq_func = Gf;
    this.write_func = Hf;
    this.receive_fifo = "";
    ff.register_ioport_write(0x3f8, 8, 1, this.ioport_write.bind(this));
    ff.register_ioport_read(0x3f8, 8, 1, this.ioport_read.bind(this));
}
Ff.prototype.update_irq = function () {
    if ((this.lsr & 0x01) && (this.ier & 0x01)) {
        this.iir = 0x04;
    } else if ((this.lsr & 0x20) && (this.ier & 0x02)) {
        this.iir = 0x02;
    } else {
        this.iir = 0x01;
    }
    if (this.iir != 0x01) {
        this.set_irq_func(1);
    } else {
        this.set_irq_func(0);
    }
};
Ff.prototype.ioport_write = function (ia, ja) {
    ia &= 7;
    switch (ia) {
    default:
    case 0:
        if (this.lcr & 0x80) {
            this.divider = (this.divider & 0xff00) | ja;
        } else {
            this.lsr &= ~0x20;
            this.update_irq();
            this.write_func(String.fromCharCode(ja));
            this.lsr |= 0x20;
            this.lsr |= 0x40;
            this.update_irq();
        }
        break;
    case 1:
        if (this.lcr & 0x80) {
            this.divider = (this.divider & 0x00ff) | (ja << 8);
        } else {
            this.ier = ja;
            this.update_irq();
        }
        break;
    case 2:
        break;
    case 3:
        this.lcr = ja;
        break;
    case 4:
        this.mcr = ja;
        break;
    case 5:
        break;
    case 6:
        this.msr = ja;
        break;
    case 7:
        this.scr = ja;
        break;
    }
};
Ff.prototype.ioport_read = function (ia) {
    var hf;
    ia &= 7;
    switch (ia) {
    default:
    case 0:
        if (this.lcr & 0x80) {
            hf = this.divider & 0xff;
        } else {
            hf = this.rbr;
            this.lsr &= ~ (0x01 | 0x10);
            this.update_irq();
            this.send_char_from_fifo();
        }
        break;
    case 1:
        if (this.lcr & 0x80) {
            hf = (this.divider >> 8) & 0xff;
        } else {
            hf = this.ier;
        }
        break;
    case 2:
        hf = this.iir;
        break;
    case 3:
        hf = this.lcr;
        break;
    case 4:
        hf = this.mcr;
        break;
    case 5:
        hf = this.lsr;
        break;
    case 6:
        hf = this.msr;
        break;
    case 7:
        hf = this.scr;
        break;
    }
    return hf;
};
Ff.prototype.send_break = function () {
    this.rbr = 0;
    this.lsr |= 0x10 | 0x01;
    this.update_irq();
};
Ff.prototype.send_char = function (If) {
    this.rbr = If;
    this.lsr |= 0x01;
    this.update_irq();
};
Ff.prototype.send_char_from_fifo = function () {
    var Jf;
    Jf = this.receive_fifo;
    if (Jf != "" && !(this.lsr & 0x01)) {
        this.send_char(Jf.charCodeAt(0));
        this.receive_fifo = Jf.substr(1, Jf.length - 1);
    }
};
Ff.prototype.send_chars = function (qa) {
    this.receive_fifo += qa;
    this.send_char_from_fifo();
};

function Kf(ff, Lf) {
    ff.register_ioport_read(0x64, 1, 1, this.read_status.bind(this));
    ff.register_ioport_write(0x64, 1, 1, this.write_command.bind(this));
    this.reset_request = Lf;
}
Kf.prototype.read_status = function (ia) {
    return 0;
};
Kf.prototype.write_command = function (ia, ja) {
    switch (ja) {
    case 0xfe:
        this.reset_request();
        break;
    default:
        break;
    }
};

function Mf(ff, kf, Nf, Hf, Of) {
    ff.register_ioport_read(kf, 16, 4, this.ioport_readl.bind(this));
    ff.register_ioport_write(kf, 16, 4, this.ioport_writel.bind(this));
    ff.register_ioport_read(kf + 8, 1, 1, this.ioport_readb.bind(this));
    ff.register_ioport_write(kf + 8, 1, 1, this.ioport_writeb.bind(this));
    this.cur_pos = 0;
    this.doc_str = "";
    this.read_func = Nf;
    this.write_func = Hf;
    this.get_boot_time = Of;
}
Mf.prototype.ioport_writeb = function (ia, ja) {
    this.doc_str += String.fromCharCode(ja);
};
Mf.prototype.ioport_readb = function (ia) {
    var c, qa, ja;
    qa = this.doc_str;
    if (this.cur_pos < qa.length) {
        ja = qa.charCodeAt(this.cur_pos) & 0xff;
    } else {
        ja = 0;
    }
    this.cur_pos++;
    return ja;
};
Mf.prototype.ioport_writel = function (ia, ja) {
    var qa;
    ia = (ia >> 2) & 3;
    switch (ia) {
    case 0:
        this.doc_str = this.doc_str.substr(0, ja >>> 0);
        break;
    case 1:
        return this.cur_pos = ja >>> 0;
    case 2:
        qa = String.fromCharCode(ja & 0xff) + String.fromCharCode((ja >> 8) & 0xff) + String.fromCharCode((ja >> 16) & 0xff) + String.fromCharCode((ja >> 24) & 0xff);
        this.doc_str += qa;
        break;
    case 3:
        this.write_func(this.doc_str);
    }
};
Mf.prototype.ioport_readl = function (ia) {
    var ja;
    ia = (ia >> 2) & 3;
    switch (ia) {
    case 0:
        this.doc_str = this.read_func();
        return this.doc_str.length >> 0;
    case 1:
        return this.cur_pos >> 0;
    case 2:
        ja = this.ioport_readb(0);
        ja |= this.ioport_readb(0) << 8;
        ja |= this.ioport_readb(0) << 16;
        ja |= this.ioport_readb(0) << 24;
        return ja;
    case 3:
        if (this.get_boot_time) return this.get_boot_time() >> 0;
        else return 0;
    }
};

function tf(mf) {
    this.hard_irq = mf;
}
function Pf() {
    return this.cycle_count;
}
function PCEmulator(Qf) {
    var za;
    za = new CPU_X86();
    this.cpu = za;
    za.phys_mem_resize(Qf.mem_size);
    this.init_ioports();
    this.register_ioport_write(0x80, 1, 1, this.ioport80_write);
    this.pic = new rf(this, 0x20, 0xa0, tf.bind(za));
    this.pit = new vf(this, this.pic.set_irq.bind(this.pic, 0), Pf.bind(za));
    this.cmos = new ef(this);
    this.serial = new Ff(this, 0x3f8, this.pic.set_irq.bind(this.pic, 4), Qf.serial_write);
    this.kbd = new Kf(this, this.reset.bind(this));
    this.reset_request = 0;
    if (Qf.clipboard_get && Qf.clipboard_set) {
        this.jsclipboard = new Mf(this, 0x3c0, Qf.clipboard_get, Qf.clipboard_set, Qf.get_boot_time);
    }
    za.ld8_port = this.ld8_port.bind(this);
    za.ld16_port = this.ld16_port.bind(this);
    za.ld32_port = this.ld32_port.bind(this);
    za.st8_port = this.st8_port.bind(this);
    za.st16_port = this.st16_port.bind(this);
    za.st32_port = this.st32_port.bind(this);
    za.get_hard_intno = this.pic.get_hard_intno.bind(this.pic);
}
PCEmulator.prototype.load_binary = function (Ye, ka) {
    return this.cpu.load_binary(Ye, ka);
};
PCEmulator.prototype.start = function () {
    setTimeout(this.timer_func.bind(this), 10);
};
PCEmulator.prototype.timer_func = function () {
    var Oa, Rf, Sf, Tf, Uf, ff, za;
    ff = this;
    za = ff.cpu;
    Sf = za.cycle_count + 100000;
    Tf = false;
    Uf = false;
    Vf: while (za.cycle_count < Sf) {
        ff.pit.update_irq();
        Oa = za.exec(Sf - za.cycle_count);
        if (Oa == 256) {
            if (ff.reset_request) {
                Tf = true;
                break;
            }
        } else if (Oa == 257) {
            Uf = true;
            break;
        } else {
            Tf = true;
            break;
        }
    }
    if (!Tf) {
        if (Uf) {
            setTimeout(this.timer_func.bind(this), 10);
        } else {
            setTimeout(this.timer_func.bind(this), 0);
        }
    }
};
PCEmulator.prototype.init_ioports = function () {
    var i, Wf, Xf;
    this.ioport_readb_table = new Array();
    this.ioport_writeb_table = new Array();
    this.ioport_readw_table = new Array();
    this.ioport_writew_table = new Array();
    this.ioport_readl_table = new Array();
    this.ioport_writel_table = new Array();
    Wf = this.default_ioport_readw.bind(this);
    Xf = this.default_ioport_writew.bind(this);
    for (i = 0; i < 1024; i++) {
        this.ioport_readb_table[i] = this.default_ioport_readb;
        this.ioport_writeb_table[i] = this.default_ioport_writeb;
        this.ioport_readw_table[i] = Wf;
        this.ioport_writew_table[i] = Xf;
        this.ioport_readl_table[i] = this.default_ioport_readl;
        this.ioport_writel_table[i] = this.default_ioport_writel;
    }
};
PCEmulator.prototype.default_ioport_readb = function (kf) {
    var ja;
    ja = 0xff;
    return ja;
};
PCEmulator.prototype.default_ioport_readw = function (kf) {
    var ja;
    ja = this.ioport_readb_table[kf](kf);
    kf = (kf + 1) & (1024 - 1);
    ja |= this.ioport_readb_table[kf](kf) << 8;
    return ja;
};
PCEmulator.prototype.default_ioport_readl = function (kf) {
    var ja;
    ja = -1;
    return ja;
};
PCEmulator.prototype.default_ioport_writeb = function (kf, ja) {};
PCEmulator.prototype.default_ioport_writew = function (kf, ja) {
    this.ioport_writeb_table[kf](kf, ja & 0xff);
    kf = (kf + 1) & (1024 - 1);
    this.ioport_writeb_table[kf](kf, (ja >> 8) & 0xff);
};
PCEmulator.prototype.default_ioport_writel = function (kf, ja) {};
PCEmulator.prototype.ld8_port = function (kf) {
    var ja;
    ja = this.ioport_readb_table[kf & (1024 - 1)](kf);
    return ja;
};
PCEmulator.prototype.ld16_port = function (kf) {
    var ja;
    ja = this.ioport_readw_table[kf & (1024 - 1)](kf);
    return ja;
};
PCEmulator.prototype.ld32_port = function (kf) {
    var ja;
    ja = this.ioport_readl_table[kf & (1024 - 1)](kf);
    return ja;
};
PCEmulator.prototype.st8_port = function (kf, ja) {
    this.ioport_writeb_table[kf & (1024 - 1)](kf, ja);
};
PCEmulator.prototype.st16_port = function (kf, ja) {
    this.ioport_writew_table[kf & (1024 - 1)](kf, ja);
};
PCEmulator.prototype.st32_port = function (kf, ja) {
    this.ioport_writel_table[kf & (1024 - 1)](kf, ja);
};
PCEmulator.prototype.register_ioport_read = function (start, dd, Yf, Zf) {
    var i;
    switch (Yf) {
    case 1:
        for (i = start; i < start + dd; i++) {
            this.ioport_readb_table[i] = Zf;
        }
        break;
    case 2:
        for (i = start; i < start + dd; i += 2) {
            this.ioport_readw_table[i] = Zf;
        }
        break;
    case 4:
        for (i = start; i < start + dd; i += 4) {
            this.ioport_readl_table[i] = Zf;
        }
        break;
    }
};
PCEmulator.prototype.register_ioport_write = function (start, dd, Yf, Zf) {
    var i;
    switch (Yf) {
    case 1:
        for (i = start; i < start + dd; i++) {
            this.ioport_writeb_table[i] = Zf;
        }
        break;
    case 2:
        for (i = start; i < start + dd; i += 2) {
            this.ioport_writew_table[i] = Zf;
        }
        break;
    case 4:
        for (i = start; i < start + dd; i += 4) {
            this.ioport_writel_table[i] = Zf;
        }
        break;
    }
};
PCEmulator.prototype.ioport80_write = function (ia, af) {};
PCEmulator.prototype.reset = function () {
    this.request_request = 1;
};