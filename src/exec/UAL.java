package exec;

import cpu.CPU6809;

public class UAL {
    private CPU6809 cpu;
    
    public UAL(CPU6809 cpu) {
        this.cpu = cpu;
    }
    
    public int calculerAdd8(int reg, int val, boolean carryIn) {
        int c = (carryIn && (cpu.getRegistres().getCC() & 1) != 0) ? 1 : 0;
        int res = reg + val + c;
        int cc = cpu.getRegistres().getCC() & ~0x2F;
        if ((res & 0x100) != 0) cc |= 1; // C
        if ((res & 0xFF) == 0) cc |= 4; // Z
        if ((res & 0x80) != 0) cc |= 8; // N
        if (((reg ^ res) & (val ^ res) & 0x80) != 0) cc |= 2; // V
        if (((reg & 0xF) + (val & 0xF) + c) > 0xF) cc |= 0x20; // H
        cpu.getRegistres().setCC(cc); 
        return res & 0xFF;
    }

    public int calculerAdd16(int reg, int val) {
        int res = reg + val;
        int cc = cpu.getRegistres().getCC() & ~0x0F;
        if ((res & 0x10000) != 0) cc |= 1;
        if ((res & 0xFFFF) == 0) cc |= 4;
        if ((res & 0x8000) != 0) cc |= 8;
        if (((reg ^ res) & (val ^ res) & 0x8000) != 0) cc |= 2;
        cpu.getRegistres().setCC(cc); 
        return res & 0xFFFF;
    }

    public int calculerSub8(int reg, int val, boolean borrowIn) {
        int b = (borrowIn && (cpu.getRegistres().getCC() & 1) != 0) ? 1 : 0;
        int res = reg - val - b;
        int cc = cpu.getRegistres().getCC() & ~0x0F;
        if (res < 0 || (reg < val)) cc |= 1; // C
        if ((res & 0xFF) == 0) cc |= 4; // Z
        if ((res & 0x80) != 0) cc |= 8; // N
        if (((reg ^ val) & (reg ^ res) & 0x80) != 0) cc |= 2; // V
        cpu.getRegistres().setCC(cc); 
        return res & 0xFF;
    }

    public int calculerSub16(int reg, int val) {
        int res = reg - val;
        int cc = cpu.getRegistres().getCC() & ~0x0F;
        if ((res & 0x10000) != 0) cc |= 1;
        if ((res & 0xFFFF) == 0) cc |= 4;
        if ((res & 0x8000) != 0) cc |= 8;
        if (((reg ^ val) & (reg ^ res) & 0x8000) != 0) cc |= 2;
        cpu.getRegistres().setCC(cc); 
        return res & 0xFFFF;
    }

    public int calculerLogique(int reg, int val, char op) {
        int res = (op == '&') ? (reg & val) : (op == '|') ? (reg | val) : (reg ^ val);
        int cc = cpu.getRegistres().getCC() & ~0x0E;
        if (res == 0) cc |= 4;
        if ((res & 0x80) != 0) cc |= 8;
        cpu.getRegistres().setCC(cc); 
        return res & 0xFF;
    }

    public int calculerNeg(int val) {
        int res = (0 - val) & 0xFF;
        int cc = cpu.getRegistres().getCC() & ~0x0F;
        if (val != 0) cc |= 1; // C=1 sauf si 0
        if (res == 0) cc |= 4;
        if ((res & 0x80) != 0) cc |= 8;
        if (val == 0x80) cc |= 2; // V
        cpu.getRegistres().setCC(cc); 
        return res;
    }

    public int calculerCom(int val) {
        int res = (~val) & 0xFF;
        int cc = cpu.getRegistres().getCC() & ~0x0E;
        cc |= 1; // C=1 toujours
        if (res == 0) cc |= 4;
        if ((res & 0x80) != 0) cc |= 8;
        cpu.getRegistres().setCC(cc); 
        return res;
    }

    public int calculerShift(int val, char dir, boolean arith) {
        int cc = cpu.getRegistres().getCC() & ~0x0F;
        int res = 0, cOut = 0;
        if (dir == 'L') { 
            cOut = (val >> 7) & 1; 
            res = (val << 1) & 0xFF; 
        } else { 
            cOut = val & 1; 
            res = (arith) ? ((val >> 1) | (val & 0x80)) : (val >> 1); 
        }
        if (cOut == 1) cc |= 1;
        if (res == 0) cc |= 4;
        if ((res & 0x80) != 0) cc |= 8;
        if (dir == 'L' && ((val >> 7) ^ (res >> 7)) != 0) cc |= 2; // V pour ASL
        cpu.getRegistres().setCC(cc); 
        return res;
    }

    public int calculerRotate(int val, char dir) {
        int cIn = cpu.getRegistres().getCC() & 1;
        int res = 0, cOut = 0;
        if (dir == 'L') { 
            cOut = (val >> 7) & 1; 
            res = ((val << 1) | cIn) & 0xFF; 
        } else { 
            cOut = val & 1; 
            res = ((val >> 1) | (cIn << 7)) & 0xFF; 
        }
        int cc = cpu.getRegistres().getCC() & ~0x0F;
        if (cOut == 1) cc |= 1;
        if (res == 0) cc |= 4;
        if ((res & 0x80) != 0) cc |= 8;
        cpu.getRegistres().setCC(cc); 
        return res;
    }
}