package exec;

import cpu.CPU6809;
import mem.Memoire;

public class GestionnaireAdressage {
    private CPU6809 cpu;
    private Memoire mem;
    
    public GestionnaireAdressage(CPU6809 cpu, Memoire mem) {
        this.cpu = cpu;
        this.mem = mem;
    }
    
    public int lireOctetImmediat() { 
        int pc = cpu.getRegistres().getPC();
        int v = mem.lire(pc); 
        cpu.getRegistres().setPC(pc + 1); 
        return v; 
    }
    
    public int lireMotImmediat() { 
        int v = mem.lireMot(cpu.getRegistres().getPC()); 
        cpu.getRegistres().setPC(cpu.getRegistres().getPC() + 2); 
        return v; 
    }
    
    public int getAdrsDirect() { 
        int v = mem.lire(cpu.getRegistres().getPC()); 
        cpu.getRegistres().setPC(cpu.getRegistres().getPC() + 1); 
        return (cpu.getRegistres().getDP() << 8) | v; 
    }
    
    
    public int getAdrsEtendu(boolean indirect) {
        int ptrAdresse = mem.lireMot(cpu.getRegistres().getPC()); 
        cpu.getRegistres().setPC(cpu.getRegistres().getPC() + 2); 
        
        if (indirect) {
            int adresseEffective = mem.lireMot(ptrAdresse);
            return adresseEffective;
        } else {
            return ptrAdresse;
        }
    }
    public int getAdrsEtenduDirect() { 
    	return getAdrsEtendu(false);
    }
    
 
    public int getAdrsIndexe() {
        int postByte = mem.lire(cpu.getRegistres().getPC()); 
        cpu.getRegistres().setPC(cpu.getRegistres().getPC() + 1);
        
        // Vérifier si c'est un mode sans offset (5 bits)
        if ((postByte & 0x80) == 0) {
            // Mode 5-bit offset
            int r = 0;
            switch((postByte >> 5) & 3) {
                case 0: r = cpu.getRegistres().getX(); break;
                case 1: r = cpu.getRegistres().getY(); break;
                case 2: r = cpu.getRegistres().getU(); break;
                case 3: r = cpu.getRegistres().getS(); break;
            }
            int off = postByte & 0x1F;
            if ((off & 0x10) != 0) off |= 0xFFFFFFE0; // Sign extension pour 5 bits
            return (r + off) & 0xFFFF;
        } else {
            // Mode avec offset étendu ou registre
            int r = 0;
            switch((postByte >> 5) & 3) {
                case 0: r = cpu.getRegistres().getX(); break;
                case 1: r = cpu.getRegistres().getY(); break;
                case 2: r = cpu.getRegistres().getU(); break;
                case 3: r = cpu.getRegistres().getS(); break;
            }
            
            int mode = postByte & 0x1F;
            
            // Vérifier les différents modes
            switch (mode) {
                case 0x00: // ,R+
                    int adr = r;
                    // Auto-incrémentation
                    switch((postByte >> 5) & 3) {
                        case 0: cpu.getRegistres().setX((r + 1) & 0xFFFF); break;
                        case 1: cpu.getRegistres().setY((r + 1) & 0xFFFF); break;
                        case 2: cpu.getRegistres().setU((r + 1) & 0xFFFF); break;
                        case 3: cpu.getRegistres().setS((r + 1) & 0xFFFF); break;
                    }
                    return adr;
                    
                case 0x01: // ,R++
                    adr = r;
                    switch((postByte >> 5) & 3) {
                        case 0: cpu.getRegistres().setX((r + 2) & 0xFFFF); break;
                        case 1: cpu.getRegistres().setY((r + 2) & 0xFFFF); break;
                        case 2: cpu.getRegistres().setU((r + 2) & 0xFFFF); break;
                        case 3: cpu.getRegistres().setS((r + 2) & 0xFFFF); break;
                    }
                    return adr;
                    
                case 0x02: // -,R
                    switch((postByte >> 5) & 3) {
                        case 0: r = (cpu.getRegistres().getX() - 1) & 0xFFFF; cpu.getRegistres().setX(r); break;
                        case 1: r = (cpu.getRegistres().getY() - 1) & 0xFFFF; cpu.getRegistres().setY(r); break;
                        case 2: r = (cpu.getRegistres().getU() - 1) & 0xFFFF; cpu.getRegistres().setU(r); break;
                        case 3: r = (cpu.getRegistres().getS() - 1) & 0xFFFF; cpu.getRegistres().setS(r); break;
                    }
                    return r;
                    
                case 0x03: // --,R
                    switch((postByte >> 5) & 3) {
                        case 0: r = (cpu.getRegistres().getX() - 2) & 0xFFFF; cpu.getRegistres().setX(r); break;
                        case 1: r = (cpu.getRegistres().getY() - 2) & 0xFFFF; cpu.getRegistres().setY(r); break;
                        case 2: r = (cpu.getRegistres().getU() - 2) & 0xFFFF; cpu.getRegistres().setU(r); break;
                        case 3: r = (cpu.getRegistres().getS() - 2) & 0xFFFF; cpu.getRegistres().setS(r); break;
                    }
                    return r;
                    
                case 0x04: // ,R (zéro offset)
                    return r;
                    
                case 0x05: // B,R
                    return (r + (cpu.getRegistres().getB() & 0xFF)) & 0xFFFF;
                    
                case 0x06: // A,R
                    return (r + (cpu.getRegistres().getA() & 0xFF)) & 0xFFFF;
                    
                case 0x08: // 8-bit offset
                    int offset8 = mem.lire(cpu.getRegistres().getPC());
                    cpu.getRegistres().setPC(cpu.getRegistres().getPC() + 1);
                    if ((offset8 & 0x80) != 0) offset8 |= 0xFFFFFF00; // Sign extension
                    return (r + offset8) & 0xFFFF;
                    
                case 0x09: // 16-bit offset
                    int offset16 = mem.lireMot(cpu.getRegistres().getPC());
                    cpu.getRegistres().setPC(cpu.getRegistres().getPC() + 2);
                    return (r + offset16) & 0xFFFF;
                    
                case 0x0B: // D,R
                    int d = (cpu.getRegistres().getA() << 8) | (cpu.getRegistres().getB() & 0xFF);
                    return (r + d) & 0xFFFF;
                    
                case 0x0C: // PC avec 8-bit offset
                    int pcOffset8 = mem.lire(cpu.getRegistres().getPC());
                    cpu.getRegistres().setPC(cpu.getRegistres().getPC() + 1);
                    if ((pcOffset8 & 0x80) != 0) pcOffset8 |= 0xFFFFFF00;
                    return (cpu.getRegistres().getPC() + pcOffset8) & 0xFFFF;
                    
                case 0x0D: // PC avec 16-bit offset
                    int pcOffset16 = mem.lireMot(cpu.getRegistres().getPC());
                    cpu.getRegistres().setPC(cpu.getRegistres().getPC() + 2);
                    return (cpu.getRegistres().getPC() + pcOffset16) & 0xFFFF;
                    
                default:
                    System.out.println("Mode indexé non supporté: " + Integer.toHexString(mode));
                    return 0;
            }
        }
    }
}
