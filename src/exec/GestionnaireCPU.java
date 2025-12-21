package exec;

import cpu.CPU6809;
import mem.Memoire;

public class GestionnaireCPU {
    private CPU6809 cpu;
    private Memoire mem;
    
    public GestionnaireCPU(CPU6809 cpu, Memoire mem) {
        this.cpu = cpu;
        this.mem = mem;
    }
    
    // --- Gestion D (A:B) ---
    public int getD() { 
        return (cpu.getRegistres().getA() << 8) | cpu.getRegistres().getB(); 
    }
    
    public void setD(int val) { 
        cpu.getRegistres().setA((val >> 8) & 0xFF); 
        cpu.getRegistres().setB(val & 0xFF); 
    }
    
    // --- Gestion des flags ---
    public void updateFlags8(int v) {
        int cc = cpu.getRegistres().getCC() & 0xF0;
        if ((v & 0xFF) == 0) cc |= 0x04;//Z flag (bit 2)
        if ((v & 0x80) != 0) cc |= 0x08;//N flag (bit 3)
        cpu.getRegistres().setCC(cc);
    }
    
    public void updateFlags16(int v) {
        int cc = cpu.getRegistres().getCC() & 0xF0;
        if ((v & 0xFFFF) == 0) cc |= 0x04;
        if ((v & 0x8000) != 0) cc |= 0x08;
        cpu.getRegistres().setCC(cc);
    }
    
    public void updateFlagsTst(int v) { 
        updateFlags8(v); 
        cpu.getRegistres().setCC(cpu.getRegistres().getCC() & ~2); // V=0
    }
    
    public void updateFlagsZ(int v) { 
        if (v == 0) 
            cpu.getRegistres().setCC(cpu.getRegistres().getCC() | 4); 
        else 
            cpu.getRegistres().setCC(cpu.getRegistres().getCC() & ~4); 
    }
    
    // --- Gestion de la pile ---
    public void pushWordS(int v) {
        cpu.getRegistres().setS((cpu.getRegistres().getS() - 1) & 0xFFFF); 
        mem.ecrire(cpu.getRegistres().getS(), v & 0xFF);
        cpu.getRegistres().setS((cpu.getRegistres().getS() - 1) & 0xFFFF); 
        mem.ecrire(cpu.getRegistres().getS(), (v >> 8) & 0xFF);
    }
    
    public int pullWordS() {
        int h = mem.lire(cpu.getRegistres().getS()); 
        cpu.getRegistres().setS((cpu.getRegistres().getS() + 1) & 0xFFFF);
        int l = mem.lire(cpu.getRegistres().getS()); 
        cpu.getRegistres().setS((cpu.getRegistres().getS() + 1) & 0xFFFF);
        return (h << 8) | l;
    }
    
    public void pushByteS(int v) {
        cpu.getRegistres().setS((cpu.getRegistres().getS() - 1) & 0xFFFF); 
        mem.ecrire(cpu.getRegistres().getS(), v & 0xFF);
    }

    public int pullByteS() {
        int v = mem.lire(cpu.getRegistres().getS()); 
        cpu.getRegistres().setS((cpu.getRegistres().getS() + 1) & 0xFFFF);
        return v;
    }

    // --- Pour la pile utilisateur (U) ---
    public void pushWordU(int v) {
        cpu.getRegistres().setU((cpu.getRegistres().getU() - 1) & 0xFFFF); 
        mem.ecrire(cpu.getRegistres().getU(), v & 0xFF);
        cpu.getRegistres().setU((cpu.getRegistres().getU() - 1) & 0xFFFF); 
        mem.ecrire(cpu.getRegistres().getU(), (v >> 8) & 0xFF);
    }

    public int pullWordU() {
        int h = mem.lire(cpu.getRegistres().getU()); 
        cpu.getRegistres().setU((cpu.getRegistres().getU() + 1) & 0xFFFF);
        int l = mem.lire(cpu.getRegistres().getU()); 
        cpu.getRegistres().setU((cpu.getRegistres().getU() + 1) & 0xFFFF);
        return (h << 8) | l;
    }

    public void pushByteU(int v) {
        cpu.getRegistres().setU((cpu.getRegistres().getU() - 1) & 0xFFFF); 
        mem.ecrire(cpu.getRegistres().getU(), v & 0xFF);
    }

    public int pullByteU() {
        int v = mem.lire(cpu.getRegistres().getU()); 
        cpu.getRegistres().setU((cpu.getRegistres().getU() + 1) & 0xFFFF);
        return v;
    }
    
    
 // --- Transfert et échange de registres (pour EXG et TFR) ---
    public void transfererRegistres(int pb) {
        int srcReg = (pb >> 4) & 0xF;
        int dstReg = pb & 0xF;
        
        int value = getRegisterValue(srcReg);
        setRegisterValue(dstReg, value);
    }

    public void echangerRegistres(int pb) {
        int reg1 = (pb >> 4) & 0xF;
        int reg2 = pb & 0xF;
        
        int value1 = getRegisterValue(reg1);
        int value2 = getRegisterValue(reg2);
        
        setRegisterValue(reg1, value2);
        setRegisterValue(reg2, value1);
    }

    private int getRegisterValue(int regCode) {
        switch (regCode) {
            case 0: return getD(); // D
            case 1: return cpu.getRegistres().getX();
            case 2: return cpu.getRegistres().getY();
            case 3: return cpu.getRegistres().getU();
            case 4: return cpu.getRegistres().getS();
            case 5: return cpu.getRegistres().getPC();
            case 8: return cpu.getRegistres().getA() & 0xFF;
            case 9: return cpu.getRegistres().getB() & 0xFF;
            case 10: return cpu.getRegistres().getCC() & 0xFF;
            case 11: return cpu.getRegistres().getDP() & 0xFF;
            default: return 0;
        }
    }

    private void setRegisterValue(int regCode, int value) {
        switch (regCode) {
            case 0: setD(value); break; // D
            case 1: cpu.getRegistres().setX(value); break;
            case 2: cpu.getRegistres().setY(value); break;
            case 3: cpu.getRegistres().setU(value); break;
            case 4: cpu.getRegistres().setS(value); break;
            case 5: cpu.getRegistres().setPC(value); break;
            case 8: cpu.getRegistres().setA(value & 0xFF); break;
            case 9: cpu.getRegistres().setB(value & 0xFF); break;
            case 10: cpu.getRegistres().setCC(value & 0xFF); break;
            case 11: cpu.getRegistres().setDP(value & 0xFF); break;
        }
    }
}