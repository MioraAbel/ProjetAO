package exec;

import cpu.CPU6809;
import mem.Memoire;

public class UniteExecution {
    private CPU6809 cpu;
    private Memoire mem;
    private GestionnaireAdressage adressage;
    private UAL calculateur;
    private GestionnaireCPU gestionnaire;
    private boolean etenduIndirect = false;  //stocker si le mode est indirect
    
    public UniteExecution(CPU6809 cpu, Memoire mem) {
        this.cpu = cpu;
        this.mem = mem;
        this.adressage = new GestionnaireAdressage(cpu, mem);
        this.calculateur = new UAL(cpu);
        this.gestionnaire = new GestionnaireCPU(cpu, mem);
    }
    
    public void setEtenduIndirect(boolean indirect) {
        this.etenduIndirect = indirect;
    }

    public void executerInstruction(int opcode) {
        // Variables temporaires
        int val, adr, res;
        etenduIndirect = false;

        switch (opcode) {
        	// --- ABX (Add B to X) Unsigned ---
        	case 0x3A:
            cpu.getRegistres().setX((cpu.getRegistres().getX() + (cpu.getRegistres().getB() & 0xFF)) & 0xFFFF);
            break;
        
            // --- ADCA (Add with Carry to A) ---
            case 0x89: cpu.getRegistres().setA(calculateur.calculerAdd8(cpu.getRegistres().getA(), adressage.lireOctetImmediat(), true)); break;
            case 0x99: cpu.getRegistres().setA(calculateur.calculerAdd8(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsDirect()), true)); break;
            case 0xA9: cpu.getRegistres().setA(calculateur.calculerAdd8(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsIndexe()), true)); break;
            case 0xB9: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setA(calculateur.calculerAdd8(cpu.getRegistres().getA(), mem.lire(adr), true)); 
                break;
                
            // --- ADCB (Add with Carry to B) ---
            case 0xC9: cpu.getRegistres().setB(calculateur.calculerAdd8(cpu.getRegistres().getB(), adressage.lireOctetImmediat(), true)); break;
            case 0xD9: cpu.getRegistres().setB(calculateur.calculerAdd8(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsDirect()), true)); break;
            case 0xE9: cpu.getRegistres().setB(calculateur.calculerAdd8(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsIndexe()), true)); break;
            case 0xF9: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setB(calculateur.calculerAdd8(cpu.getRegistres().getB(), mem.lire(adr), true)); 
                break;
                
            // --- ADDA (Add Memory to A) ---
            case 0x8B: cpu.getRegistres().setA(calculateur.calculerAdd8(cpu.getRegistres().getA(), adressage.lireOctetImmediat(), false)); break;
            case 0x9B: cpu.getRegistres().setA(calculateur.calculerAdd8(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsDirect()), false)); break;
            case 0xAB: cpu.getRegistres().setA(calculateur.calculerAdd8(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsIndexe()), false)); break;
            case 0xBB: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setA(calculateur.calculerAdd8(cpu.getRegistres().getA(), mem.lire(adr), false)); 
                break;
                
            // --- ADDB (Add Memory to B) ---
            case 0xCB: cpu.getRegistres().setB(calculateur.calculerAdd8(cpu.getRegistres().getB(), adressage.lireOctetImmediat(), false)); break;
            case 0xDB: cpu.getRegistres().setB(calculateur.calculerAdd8(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsDirect()), false)); break;
            case 0xEB: cpu.getRegistres().setB(calculateur.calculerAdd8(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsIndexe()), false)); break;
            case 0xFB: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setB(calculateur.calculerAdd8(cpu.getRegistres().getB(), mem.lire(adr), false)); 
                break;
                
            // --- ADDD (Add Memory to D - 16 bits) ---
            case 0xC3: gestionnaire.setD(calculateur.calculerAdd16(gestionnaire.getD(), adressage.lireMotImmediat())); break;
            case 0xD3: gestionnaire.setD(calculateur.calculerAdd16(gestionnaire.getD(), mem.lireMot(adressage.getAdrsDirect()))); break;
            case 0xE3: gestionnaire.setD(calculateur.calculerAdd16(gestionnaire.getD(), mem.lireMot(adressage.getAdrsIndexe()))); break;
            case 0xF3: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                gestionnaire.setD(calculateur.calculerAdd16(gestionnaire.getD(), mem.lireMot(adr))); 
                break;
                
            // --- ANDA (Logical AND A) ---
            case 0x84: cpu.getRegistres().setA(calculateur.calculerLogique(cpu.getRegistres().getA(), adressage.lireOctetImmediat(), '&')); break;
            case 0x94: cpu.getRegistres().setA(calculateur.calculerLogique(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsDirect()), '&')); break;
            case 0xA4: cpu.getRegistres().setA(calculateur.calculerLogique(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsIndexe()), '&')); break;
            case 0xB4: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setA(calculateur.calculerLogique(cpu.getRegistres().getA(), mem.lire(adr), '&'));
                break;
                
            // --- ANDB (Logical AND B) ---
            case 0xC4: cpu.getRegistres().setB(calculateur.calculerLogique(cpu.getRegistres().getB(), adressage.lireOctetImmediat(), '&')); break;
            case 0xD4: cpu.getRegistres().setB(calculateur.calculerLogique(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsDirect()), '&')); break;
            case 0xE4: cpu.getRegistres().setB(calculateur.calculerLogique(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsIndexe()), '&')); break;
            case 0xF4: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setB(calculateur.calculerLogique(cpu.getRegistres().getB(), mem.lire(adr), '&'));
                break;
                
            // --- ANDCC (Logical AND CC) ---
            case 0x1C: cpu.getRegistres().setCC(calculateur.calculerLogique(cpu.getRegistres().getCC(), adressage.lireOctetImmediat(), '&')); break;
            
            // --- ASL / LSL (Arithmetic Shift Left) ---
            case 0x48: cpu.getRegistres().setA(calculateur.calculerShift(cpu.getRegistres().getA(), 'L', true)); break; // ASLA
            case 0x58: cpu.getRegistres().setB(calculateur.calculerShift(cpu.getRegistres().getB(), 'L', true)); break; // ASLB
            case 0x08: adr = adressage.getAdrsDirect(); mem.ecrire(adr, calculateur.calculerShift(mem.lire(adr), 'L', true)); break; // Direct
            case 0x68: adr = adressage.getAdrsIndexe(); mem.ecrire(adr, calculateur.calculerShift(mem.lire(adr), 'L', true)); break; // Indexé
            case 0x78: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrire(adr, calculateur.calculerShift(mem.lire(adr), 'L', true)); 
                break; // Etendu
            
            // --- ASR (Arithmetic Shift Right) ---
            case 0x47: cpu.getRegistres().setA(calculateur.calculerShift(cpu.getRegistres().getA(), 'R', true)); break; // ASRA
            case 0x57: cpu.getRegistres().setB(calculateur.calculerShift(cpu.getRegistres().getB(), 'R', true)); break; // ASRB
            case 0x07: adr = adressage.getAdrsDirect(); mem.ecrire(adr, calculateur.calculerShift(mem.lire(adr), 'R', true)); break;
            case 0x67: adr = adressage.getAdrsIndexe(); mem.ecrire(adr, calculateur.calculerShift(mem.lire(adr), 'R', true)); break;
            case 0x77: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrire(adr, calculateur.calculerShift(mem.lire(adr), 'R', true)); 
                break;
                
            // --- BITA (Bit Test A) ---
            case 0x85: calculateur.calculerLogique(cpu.getRegistres().getA(), adressage.lireOctetImmediat(), '&'); break;
            case 0x95: calculateur.calculerLogique(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsDirect()), '&'); break;
            case 0xA5: calculateur.calculerLogique(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsIndexe()), '&'); break;
            case 0xB5: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                calculateur.calculerLogique(cpu.getRegistres().getA(), mem.lire(adr), '&'); 
                break;
            
            // --- BITB (Bit Test B) ---
            case 0xC5: calculateur.calculerLogique(cpu.getRegistres().getB(), adressage.lireOctetImmediat(), '&'); break;
            case 0xD5: calculateur.calculerLogique(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsDirect()), '&'); break;
            case 0xE5: calculateur.calculerLogique(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsIndexe()), '&'); break;
            case 0xF5: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                calculateur.calculerLogique(cpu.getRegistres().getB(), mem.lire(adr), '&'); 
                break;  
                
            // --- CLR (Clear) ---
            case 0x4F: cpu.getRegistres().setA(0); gestionnaire.updateFlags8(0); break; // CLRA
            case 0x5F: cpu.getRegistres().setB(0); gestionnaire.updateFlags8(0); break; // CLRB
            case 0x0F: adr = adressage.getAdrsDirect(); mem.ecrire(adr, 0); gestionnaire.updateFlags8(0); break;
            case 0x6F: adr = adressage.getAdrsIndexe(); mem.ecrire(adr, 0); gestionnaire.updateFlags8(0); break;
            case 0x7F: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrire(adr, 0); 
                gestionnaire.updateFlags8(0); 
                break;
            
            // --- CMPA (Compare A) ---
            case 0x81: calculateur.calculerSub8(cpu.getRegistres().getA(), adressage.lireOctetImmediat(), false); break;
            case 0x91: calculateur.calculerSub8(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsDirect()), false); break;
            case 0xA1: calculateur.calculerSub8(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsIndexe()), false); break;
            case 0xB1: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                calculateur.calculerSub8(cpu.getRegistres().getA(), mem.lire(adr), false); 
                break;
            
            // --- CMPB (Compare B) ---
            case 0xC1: calculateur.calculerSub8(cpu.getRegistres().getB(), adressage.lireOctetImmediat(), false); break;
            case 0xD1: calculateur.calculerSub8(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsDirect()), false); break;
            case 0xE1: calculateur.calculerSub8(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsIndexe()), false); break;
            case 0xF1: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                calculateur.calculerSub8(cpu.getRegistres().getB(), mem.lire(adr), false); 
                break;
            
            // --- CMPD (Compare D) ---
            case 0x1083: calculateur.calculerSub16(gestionnaire.getD(), adressage.lireMotImmediat()); break;
            case 0x1093: calculateur.calculerSub16(gestionnaire.getD(), mem.lireMot(adressage.getAdrsDirect())); break;
            case 0x10A3: calculateur.calculerSub16(gestionnaire.getD(), mem.lireMot(adressage.getAdrsIndexe())); break;
            case 0x10B3: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                calculateur.calculerSub16(gestionnaire.getD(), mem.lireMot(adr)); 
                break;
            
            // --- CMPX (Compare X - 16 bits) ---
            case 0x8C: calculateur.calculerSub16(cpu.getRegistres().getX(), adressage.lireMotImmediat()); break;
            case 0x9C: calculateur.calculerSub16(cpu.getRegistres().getX(), mem.lireMot(adressage.getAdrsDirect())); break;
            case 0xAC: calculateur.calculerSub16(cpu.getRegistres().getX(), mem.lireMot(adressage.getAdrsIndexe())); break;
            case 0xBC: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                calculateur.calculerSub16(cpu.getRegistres().getX(), mem.lireMot(adr)); 
                break;
            
            // --- CMPY (Compare Y) ---
            case 0x108C: calculateur.calculerSub16(cpu.getRegistres().getY(), adressage.lireMotImmediat()); break;
            case 0x109C: calculateur.calculerSub16(cpu.getRegistres().getY(), mem.lireMot(adressage.getAdrsDirect())); break;
            case 0x10AC: calculateur.calculerSub16(cpu.getRegistres().getY(), mem.lireMot(adressage.getAdrsIndexe())); break;
            case 0x10BC: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                calculateur.calculerSub16(cpu.getRegistres().getY(), mem.lireMot(adr)); 
                break;
            
            // --- CMPU (Compare U) ---
            case 0x1183: calculateur.calculerSub16(cpu.getRegistres().getU(), adressage.lireMotImmediat()); break;
            case 0x1193: calculateur.calculerSub16(cpu.getRegistres().getU(), mem.lireMot(adressage.getAdrsDirect())); break;
            case 0x11A3: calculateur.calculerSub16(cpu.getRegistres().getU(), mem.lireMot(adressage.getAdrsIndexe())); break;
            case 0x11B3: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                calculateur.calculerSub16(cpu.getRegistres().getU(), mem.lireMot(adr)); 
                break;
            
            // --- CMPS (Compare S) ---
            case 0x118C: calculateur.calculerSub16(cpu.getRegistres().getS(), adressage.lireMotImmediat()); break;
            case 0x119C: calculateur.calculerSub16(cpu.getRegistres().getS(), mem.lireMot(adressage.getAdrsDirect())); break;
            case 0x11AC: calculateur.calculerSub16(cpu.getRegistres().getS(), mem.lireMot(adressage.getAdrsIndexe())); break;
            case 0x11BC: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                calculateur.calculerSub16(cpu.getRegistres().getS(), mem.lireMot(adr)); 
                break;
            
            // --- COM (Complement - One's complement) ---
            case 0x43: cpu.getRegistres().setA(calculateur.calculerCom(cpu.getRegistres().getA())); break; // COMA
            case 0x53: cpu.getRegistres().setB(calculateur.calculerCom(cpu.getRegistres().getB())); break; // COMB
            case 0x03: adr = adressage.getAdrsDirect(); mem.ecrire(adr, calculateur.calculerCom(mem.lire(adr))); break;
            case 0x63: adr = adressage.getAdrsIndexe(); mem.ecrire(adr, calculateur.calculerCom(mem.lire(adr))); break;
            case 0x73: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrire(adr, calculateur.calculerCom(mem.lire(adr))); 
                break;
            
            // --- DEC (Decrement) ---
            case 0x4A: cpu.getRegistres().setA(calculateur.calculerSub8(cpu.getRegistres().getA(), 1, false)); break; // DECA
            case 0x5A: cpu.getRegistres().setB(calculateur.calculerSub8(cpu.getRegistres().getB(), 1, false)); break; // DECB
            case 0x0A: adr = adressage.getAdrsDirect(); mem.ecrire(adr, calculateur.calculerSub8(mem.lire(adr), 1, false)); break;
            case 0x6A: adr = adressage.getAdrsIndexe(); mem.ecrire(adr, calculateur.calculerSub8(mem.lire(adr), 1, false)); break;
            case 0x7A: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrire(adr, calculateur.calculerSub8(mem.lire(adr), 1, false)); 
                break;
            
            // --- EORA (Exclusive OR A) ---
            case 0x88: cpu.getRegistres().setA(calculateur.calculerLogique(cpu.getRegistres().getA(), adressage.lireOctetImmediat(), '^')); break;
            case 0x98: cpu.getRegistres().setA(calculateur.calculerLogique(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsDirect()), '^')); break;
            case 0xA8: cpu.getRegistres().setA(calculateur.calculerLogique(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsIndexe()), '^')); break;
            case 0xB8: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setA(calculateur.calculerLogique(cpu.getRegistres().getA(), mem.lire(adr), '^')); 
                break;
            
            // --- EORB (Exclusive OR B) ---
            case 0xC8: cpu.getRegistres().setB(calculateur.calculerLogique(cpu.getRegistres().getB(), adressage.lireOctetImmediat(), '^')); break;
            case 0xD8: cpu.getRegistres().setB(calculateur.calculerLogique(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsDirect()), '^')); break;
            case 0xE8: cpu.getRegistres().setB(calculateur.calculerLogique(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsIndexe()), '^')); break;
            case 0xF8: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setB(calculateur.calculerLogique(cpu.getRegistres().getB(), mem.lire(adr), '^')); 
                break;
            
            // --- EXG (Exchange Registers) ---
            case 0x1E:
                byte postByte = (byte) mem.lire(cpu.getRegistres().getPC());
                cpu.getRegistres().setPC(cpu.getRegistres().getPC() + 1);
                gestionnaire.echangerRegistres(postByte);
                break;
            
            // --- INC (Increment) ---
            case 0x4C: cpu.getRegistres().setA(calculateur.calculerAdd8(cpu.getRegistres().getA(), 1, false)); break; // INCA
            case 0x5C: cpu.getRegistres().setB(calculateur.calculerAdd8(cpu.getRegistres().getB(), 1, false)); break; // INCB
            case 0x0C: adr = adressage.getAdrsDirect(); mem.ecrire(adr, calculateur.calculerAdd8(mem.lire(adr), 1, false)); break;
            case 0x6C: adr = adressage.getAdrsIndexe(); mem.ecrire(adr, calculateur.calculerAdd8(mem.lire(adr), 1, false)); break;
            case 0x7C: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrire(adr, calculateur.calculerAdd8(mem.lire(adr), 1, false)); 
                break;
            
            // --- JMP (Jump) ---
            case 0x0E: cpu.getRegistres().setPC(adressage.getAdrsDirect()); break; // JMP Direct
            case 0x6E: cpu.getRegistres().setPC(adressage.getAdrsIndexe()); break; // JMP Indexé
            case 0x7E: 
                cpu.getRegistres().setPC(adressage.getAdrsEtendu(etenduIndirect)); 
                break; // JMP Etendu
            
            // --- JSR (Jump to Subroutine) ---
            case 0x9D: gestionnaire.pushWordS(cpu.getRegistres().getPC()); cpu.getRegistres().setPC(adressage.getAdrsDirect()); break; // Direct
            case 0xAD: gestionnaire.pushWordS(cpu.getRegistres().getPC()); cpu.getRegistres().setPC(adressage.getAdrsIndexe()); break; // Indexé
            case 0xBD: 
                gestionnaire.pushWordS(cpu.getRegistres().getPC()); 
                cpu.getRegistres().setPC(adressage.getAdrsEtendu(etenduIndirect)); 
                break; // Etendu
            
            // --- LDA ---
            case 0x86: 
                val = adressage.lireOctetImmediat();
                cpu.getRegistres().setA(val); 
                gestionnaire.updateFlags8(cpu.getRegistres().getA()); 
                break;
            case 0x96: 
                cpu.getRegistres().setA(mem.lire(adressage.getAdrsDirect())); 
                gestionnaire.updateFlags8(cpu.getRegistres().getA()); 
                break;
            case 0xB6: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setA(mem.lire(adr)); 
                gestionnaire.updateFlags8(cpu.getRegistres().getA()); 
                break;
            case 0xA6: 
                cpu.getRegistres().setA(mem.lire(adressage.getAdrsIndexe())); 
                gestionnaire.updateFlags8(cpu.getRegistres().getA()); 
                break;
            
            // --- LDB ---
            case 0xC6: 
                cpu.getRegistres().setB(adressage.lireOctetImmediat()); 
                gestionnaire.updateFlags8(cpu.getRegistres().getB()); 
                break;
            case 0xD6: 
                cpu.getRegistres().setB(mem.lire(adressage.getAdrsDirect())); 
                gestionnaire.updateFlags8(cpu.getRegistres().getB()); 
                break;
            case 0xF6:
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setB(mem.lire(adr)); 
                gestionnaire.updateFlags8(cpu.getRegistres().getB()); 
                break;
            case 0xE6: 
                cpu.getRegistres().setB(mem.lire(adressage.getAdrsIndexe())); 
                gestionnaire.updateFlags8(cpu.getRegistres().getB()); 
                break;
            
            // --- LDD (Load D) ---
            case 0xCC: 
                gestionnaire.setD(adressage.lireMotImmediat()); 
                gestionnaire.updateFlags16(gestionnaire.getD()); 
                break;
            case 0xDC: 
                gestionnaire.setD(mem.lireMot(adressage.getAdrsDirect())); 
                gestionnaire.updateFlags16(gestionnaire.getD()); 
                break;
            case 0xFC: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                gestionnaire.setD(mem.lireMot(adr)); 
                gestionnaire.updateFlags16(gestionnaire.getD()); 
                break;
            case 0xEC: 
                gestionnaire.setD(mem.lireMot(adressage.getAdrsIndexe())); 
                gestionnaire.updateFlags16(gestionnaire.getD()); 
                break;
            
            // --- LDS (Load Stack S) ---
            case 0x10CE: 
                cpu.getRegistres().setS(adressage.lireMotImmediat()); 
                gestionnaire.updateFlags16(cpu.getRegistres().getS()); 
                break;
            case 0x10DE: 
                cpu.getRegistres().setS(mem.lireMot(adressage.getAdrsDirect())); 
                gestionnaire.updateFlags16(cpu.getRegistres().getS()); 
                break;
            case 0x10FE: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setS(mem.lireMot(adr)); 
                gestionnaire.updateFlags16(cpu.getRegistres().getS()); 
                break;
            case 0x10EE: 
                cpu.getRegistres().setS(mem.lireMot(adressage.getAdrsIndexe())); 
                gestionnaire.updateFlags16(cpu.getRegistres().getS()); 
                break;
            
            // --- LDU (Load U) ---
            case 0xCE: 
                cpu.getRegistres().setU(adressage.lireMotImmediat()); 
                gestionnaire.updateFlags16(cpu.getRegistres().getU()); 
                break;
            case 0xDE: 
                cpu.getRegistres().setU(mem.lireMot(adressage.getAdrsDirect())); 
                gestionnaire.updateFlags16(cpu.getRegistres().getU()); 
                break;
            case 0xFE: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setU(mem.lireMot(adr)); 
                gestionnaire.updateFlags16(cpu.getRegistres().getU()); 
                break;
            case 0xEE: 
                cpu.getRegistres().setU(mem.lireMot(adressage.getAdrsIndexe())); 
                gestionnaire.updateFlags16(cpu.getRegistres().getU()); 
                break;
            
            // --- LDX (Load X) ---
            case 0x8E: 
                cpu.getRegistres().setX(adressage.lireMotImmediat()); 
                gestionnaire.updateFlags16(cpu.getRegistres().getX()); 
                break;
            case 0x9E: 
                cpu.getRegistres().setX(mem.lireMot(adressage.getAdrsDirect())); 
                gestionnaire.updateFlags16(cpu.getRegistres().getX()); 
                break;
            case 0xBE: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setX(mem.lireMot(adr)); 
                gestionnaire.updateFlags16(cpu.getRegistres().getX()); 
                break;
            case 0xAE: 
                cpu.getRegistres().setX(mem.lireMot(adressage.getAdrsIndexe())); 
                gestionnaire.updateFlags16(cpu.getRegistres().getX());
                break;
            
            // --- LDY (Load Y) ---
            case 0x108E: 
                cpu.getRegistres().setY(adressage.lireMotImmediat()); 
                gestionnaire.updateFlags16(cpu.getRegistres().getY()); 
                break;
            case 0x109E: 
                cpu.getRegistres().setY(mem.lireMot(adressage.getAdrsDirect())); 
                gestionnaire.updateFlags16(cpu.getRegistres().getY()); 
                break;
            case 0x10BE: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setY(mem.lireMot(adr)); 
                gestionnaire.updateFlags16(cpu.getRegistres().getY()); 
                break;
            case 0x10AE: 
                cpu.getRegistres().setY(mem.lireMot(adressage.getAdrsIndexe())); 
                gestionnaire.updateFlags16(cpu.getRegistres().getY());
                break;
            
            // --- LEAX, LEAY, LEAS, LEAU (Load Effective Address) ---
            case 0x30: cpu.getRegistres().setX(adressage.getAdrsIndexe()); 
                gestionnaire.updateFlagsZ(cpu.getRegistres().getX()); break; // LEAX
            case 0x31: cpu.getRegistres().setY(adressage.getAdrsIndexe()); 
                gestionnaire.updateFlagsZ(cpu.getRegistres().getY()); break; // LEAY
            case 0x32: cpu.getRegistres().setS(adressage.getAdrsIndexe()); break; // LEAS
            case 0x33: cpu.getRegistres().setU(adressage.getAdrsIndexe()); break; // LEAU
            
            // --- LSR (Logical Shift Right) ---
            case 0x44: cpu.getRegistres().setA(calculateur.calculerShift(cpu.getRegistres().getA(), 'R', false)); break; // LSRA
            case 0x54: cpu.getRegistres().setB(calculateur.calculerShift(cpu.getRegistres().getB(), 'R', false)); break; // LSRB
            case 0x04: adr = adressage.getAdrsDirect(); mem.ecrire(adr, calculateur.calculerShift(mem.lire(adr), 'R', false)); break;
            case 0x64: adr = adressage.getAdrsIndexe(); mem.ecrire(adr, calculateur.calculerShift(mem.lire(adr), 'R', false)); break;
            case 0x74: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrire(adr, calculateur.calculerShift(mem.lire(adr), 'R', false)); 
                break;
            
            // --- MUL (Multiply) ---
            case 0x3D:
                int resMul = (cpu.getRegistres().getA() & 0xFF) * (cpu.getRegistres().getB() & 0xFF);
                gestionnaire.setD(resMul);
                int cc = cpu.getRegistres().getCC() & ~0x05;
                if (resMul == 0) cc |= 0x04;
                if ((resMul & 0x80) != 0) cc |= 0x01;
                cpu.getRegistres().setCC(cc);
                break;
                
            // --- NEG (Negate) ---
            case 0x40: cpu.getRegistres().setA(calculateur.calculerNeg(cpu.getRegistres().getA())); break; // NEGA
            case 0x50: cpu.getRegistres().setB(calculateur.calculerNeg(cpu.getRegistres().getB())); break; // NEGB
            case 0x00: adr = adressage.getAdrsDirect(); mem.ecrire(adr, calculateur.calculerNeg(mem.lire(adr))); break;
            case 0x60: adr = adressage.getAdrsIndexe(); mem.ecrire(adr, calculateur.calculerNeg(mem.lire(adr))); break;
            case 0x70: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrire(adr, calculateur.calculerNeg(mem.lire(adr))); 
                break;
            
            // --- NOP ---
            case 0x12: break;
            
            // --- ORA (Inclusive OR A) ---
            case 0x8A: cpu.getRegistres().setA(calculateur.calculerLogique(cpu.getRegistres().getA(), adressage.lireOctetImmediat(), '|')); break;
            case 0x9A: cpu.getRegistres().setA(calculateur.calculerLogique(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsDirect()), '|')); break;
            case 0xAA: cpu.getRegistres().setA(calculateur.calculerLogique(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsIndexe()), '|')); break;
            case 0xBA: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setA(calculateur.calculerLogique(cpu.getRegistres().getA(), mem.lire(adr), '|')); 
                break;
            
            // --- ORB (Inclusive OR B) ---
            case 0xCA: cpu.getRegistres().setB(calculateur.calculerLogique(cpu.getRegistres().getB(), adressage.lireOctetImmediat(), '|')); break;
            case 0xDA: cpu.getRegistres().setB(calculateur.calculerLogique(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsDirect()), '|')); break;
            case 0xEA: cpu.getRegistres().setB(calculateur.calculerLogique(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsIndexe()), '|')); break;
            case 0xFA: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setB(calculateur.calculerLogique(cpu.getRegistres().getB(), mem.lire(adr), '|')); 
                break;
            
            // --- ORCC ---
            case 0x1A: cpu.getRegistres().setCC(calculateur.calculerLogique(cpu.getRegistres().getCC(), adressage.lireOctetImmediat(), '|')); break;
            
            // --- ROL (Rotate Left) ---
            case 0x49: cpu.getRegistres().setA(calculateur.calculerRotate(cpu.getRegistres().getA(), 'L')); break; // ROLA
            case 0x59: cpu.getRegistres().setB(calculateur.calculerRotate(cpu.getRegistres().getB(), 'L')); break; // ROLB
            case 0x09: adr = adressage.getAdrsDirect(); mem.ecrire(adr, calculateur.calculerRotate(mem.lire(adr), 'L')); break;
            case 0x69: adr = adressage.getAdrsIndexe(); mem.ecrire(adr, calculateur.calculerRotate(mem.lire(adr), 'L')); break;
            case 0x79: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrire(adr, calculateur.calculerRotate(mem.lire(adr), 'L')); 
                break;
            
            // --- ROR (Rotate Right) ---
            case 0x46: cpu.getRegistres().setA(calculateur.calculerRotate(cpu.getRegistres().getA(), 'R')); break; // RORA
            case 0x56: cpu.getRegistres().setB(calculateur.calculerRotate(cpu.getRegistres().getB(), 'R')); break; // RORB
            case 0x06: adr = adressage.getAdrsDirect(); mem.ecrire(adr, calculateur.calculerRotate(mem.lire(adr), 'R')); break;
            case 0x66: adr = adressage.getAdrsIndexe(); mem.ecrire(adr, calculateur.calculerRotate(mem.lire(adr), 'R')); break;
            case 0x76: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrire(adr, calculateur.calculerRotate(mem.lire(adr), 'R')); 
                break;
            
             // --- RTI (Return from Interrupt) ---
            case 0x3B:
                
                // Si le bit E est positionné, on restaure tous les registres
                if ((cpu.getRegistres().getCC() & 0x80) != 0) {
                    int restoredCC = gestionnaire.pullByteS();
                    int restoredA = gestionnaire.pullByteS();
                    int restoredB = gestionnaire.pullByteS();
                    int restoredDP = gestionnaire.pullByteS();
                    int restoredX = gestionnaire.pullWordS();
                    int restoredY = gestionnaire.pullWordS();
                    int restoredU = gestionnaire.pullWordS();
                    int restoredPC = gestionnaire.pullWordS();
                    
                    cpu.getRegistres().setCC(restoredCC);
                    cpu.getRegistres().setA(restoredA);
                    cpu.getRegistres().setB(restoredB);
                    cpu.getRegistres().setDP(restoredDP);
                    cpu.getRegistres().setX(restoredX);
                    cpu.getRegistres().setY(restoredY);
                    cpu.getRegistres().setU(restoredU);
                    cpu.getRegistres().setPC(restoredPC);
                } else {
                    int restoredCC = gestionnaire.pullByteS();
                    int restoredPC = gestionnaire.pullWordS();
                    
                    cpu.getRegistres().setCC(restoredCC);
                    cpu.getRegistres().setPC(restoredPC);
                }
                break;    
                
            // --- RTS (Return from Subroutine) ---
            case 0x39: cpu.getRegistres().setPC(gestionnaire.pullWordS()); break;
            
            // --- SBCA (Subtract with Carry from A) ---
            case 0x82: cpu.getRegistres().setA(calculateur.calculerSub8(cpu.getRegistres().getA(), adressage.lireOctetImmediat(), true)); break;
            case 0x92: cpu.getRegistres().setA(calculateur.calculerSub8(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsDirect()), true)); break;
            case 0xA2: cpu.getRegistres().setA(calculateur.calculerSub8(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsIndexe()), true)); break;
            case 0xB2: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setA(calculateur.calculerSub8(cpu.getRegistres().getA(), mem.lire(adr), true)); 
                break;
            
            // --- SBCB (Subtract with Carry from B) ---
            case 0xC2: cpu.getRegistres().setB(calculateur.calculerSub8(cpu.getRegistres().getB(), adressage.lireOctetImmediat(), true)); break;
            case 0xD2: cpu.getRegistres().setB(calculateur.calculerSub8(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsDirect()), true)); break;
            case 0xE2: cpu.getRegistres().setB(calculateur.calculerSub8(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsIndexe()), true)); break;
            case 0xF2: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setB(calculateur.calculerSub8(cpu.getRegistres().getB(), mem.lire(adr), true)); 
                break;
            
            // --- SEX (Sign Extend B into A) ---
            case 0x1D:
                if ((cpu.getRegistres().getB() & 0x80) != 0) cpu.getRegistres().setA(0xFF);
                else cpu.getRegistres().setA(0x00);
                gestionnaire.updateFlags8(cpu.getRegistres().getA());
                break;
            
            // --- STA (Store A) ---
            case 0x97: mem.ecrire(adressage.getAdrsDirect(), cpu.getRegistres().getA()); gestionnaire.updateFlags8(cpu.getRegistres().getA()); break;
            case 0xB7: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrire(adr, cpu.getRegistres().getA()); 
                gestionnaire.updateFlags8(cpu.getRegistres().getA()); 
                break;
            case 0xA7: mem.ecrire(adressage.getAdrsIndexe(), cpu.getRegistres().getA()); gestionnaire.updateFlags8(cpu.getRegistres().getA()); break;
            
            // --- STB (Store B) ---
            case 0xD7: mem.ecrire(adressage.getAdrsDirect(), cpu.getRegistres().getB()); gestionnaire.updateFlags8(cpu.getRegistres().getB()); break;
            case 0xF7: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrire(adr, cpu.getRegistres().getB()); 
                gestionnaire.updateFlags8(cpu.getRegistres().getB()); 
                break;
            case 0xE7: mem.ecrire(adressage.getAdrsIndexe(), cpu.getRegistres().getB()); gestionnaire.updateFlags8(cpu.getRegistres().getB()); break;
            
            // --- STD (Store D) ---
            case 0xDD: mem.ecrireMot(adressage.getAdrsDirect(), gestionnaire.getD()); gestionnaire.updateFlags16(gestionnaire.getD()); break;
            case 0xFD: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrireMot(adr, gestionnaire.getD()); 
                gestionnaire.updateFlags16(gestionnaire.getD()); 
                break;
            case 0xED: mem.ecrireMot(adressage.getAdrsIndexe(), gestionnaire.getD()); gestionnaire.updateFlags16(gestionnaire.getD()); break;
            
            // --- STS (Store S) ---
            case 0x10DF: mem.ecrireMot(adressage.getAdrsDirect(), cpu.getRegistres().getS()); gestionnaire.updateFlags16(cpu.getRegistres().getS()); break;
            case 0x10FF: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrireMot(adr, cpu.getRegistres().getS()); 
                gestionnaire.updateFlags16(cpu.getRegistres().getS()); 
                break;
            case 0x10EF: mem.ecrireMot(adressage.getAdrsIndexe(), cpu.getRegistres().getS()); gestionnaire.updateFlags16(cpu.getRegistres().getS()); break;
            
            // --- STU (Store U) ---
            case 0xDF: mem.ecrireMot(adressage.getAdrsDirect(), cpu.getRegistres().getU()); gestionnaire.updateFlags16(cpu.getRegistres().getU()); break;
            case 0xFF: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrireMot(adr, cpu.getRegistres().getU()); 
                gestionnaire.updateFlags16(cpu.getRegistres().getU()); 
                break;
            case 0xEF: mem.ecrireMot(adressage.getAdrsIndexe(), cpu.getRegistres().getU()); gestionnaire.updateFlags16(cpu.getRegistres().getU()); break;
            
            // --- STX (Store X) ---
            case 0x9F: mem.ecrireMot(adressage.getAdrsDirect(), cpu.getRegistres().getX()); gestionnaire.updateFlags16(cpu.getRegistres().getX()); break;
            case 0xBF: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrireMot(adr, cpu.getRegistres().getX()); 
                gestionnaire.updateFlags16(cpu.getRegistres().getX()); 
                break;
            case 0xAF: mem.ecrireMot(adressage.getAdrsIndexe(), cpu.getRegistres().getX()); gestionnaire.updateFlags16(cpu.getRegistres().getX()); break;
            
            // --- STY (Store Y) ---
            case 0x109F: mem.ecrireMot(adressage.getAdrsDirect(), cpu.getRegistres().getY()); gestionnaire.updateFlags16(cpu.getRegistres().getY()); break;
            case 0x10AF: mem.ecrireMot(adressage.getAdrsIndexe(), cpu.getRegistres().getY()); gestionnaire.updateFlags16(cpu.getRegistres().getY()); break;
            case 0x10BF: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                mem.ecrireMot(adr, cpu.getRegistres().getY()); 
                gestionnaire.updateFlags16(cpu.getRegistres().getY()); 
                break;
            
            // --- SUBA ---
            case 0x80: cpu.getRegistres().setA(calculateur.calculerSub8(cpu.getRegistres().getA(), adressage.lireOctetImmediat(), false)); break;
            case 0x90: cpu.getRegistres().setA(calculateur.calculerSub8(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsDirect()), false)); break;
            case 0xA0: cpu.getRegistres().setA(calculateur.calculerSub8(cpu.getRegistres().getA(), mem.lire(adressage.getAdrsIndexe()), false)); break;
            case 0xB0: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setA(calculateur.calculerSub8(cpu.getRegistres().getA(), mem.lire(adr), false)); 
                break;
            
            // --- SUBB ---
            case 0xC0: cpu.getRegistres().setB(calculateur.calculerSub8(cpu.getRegistres().getB(), adressage.lireOctetImmediat(), false)); break;
            case 0xD0: cpu.getRegistres().setB(calculateur.calculerSub8(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsDirect()), false)); break;
            case 0xE0: cpu.getRegistres().setB(calculateur.calculerSub8(cpu.getRegistres().getB(), mem.lire(adressage.getAdrsIndexe()), false)); break;
            case 0xF0: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                cpu.getRegistres().setB(calculateur.calculerSub8(cpu.getRegistres().getB(), mem.lire(adr), false)); 
                break;
            
            // --- SUBD (16 bits) ---
            case 0x83: gestionnaire.setD(calculateur.calculerSub16(gestionnaire.getD(), adressage.lireMotImmediat())); break;
            case 0x93: gestionnaire.setD(calculateur.calculerSub16(gestionnaire.getD(), mem.lireMot(adressage.getAdrsDirect()))); break;
            case 0xA3: gestionnaire.setD(calculateur.calculerSub16(gestionnaire.getD(), mem.lireMot(adressage.getAdrsIndexe()))); break;
            case 0xB3: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                gestionnaire.setD(calculateur.calculerSub16(gestionnaire.getD(), mem.lireMot(adr))); 
                break;
             
             // --- SWI (Software Interrupt) ---
            case 0x3F: 
            	// Déterminer si E est déjà positionné
                boolean alreadyE = (cpu.getRegistres().getCC() & 0x80) != 0;
                
                if (!alreadyE) {
	                // Sauvegarde des registres sur la pile système
	            	gestionnaire.pushWordS(cpu.getRegistres().getPC());
	                gestionnaire.pushWordS(cpu.getRegistres().getU());
	                gestionnaire.pushWordS(cpu.getRegistres().getY());
	                gestionnaire.pushWordS(cpu.getRegistres().getX());
	                gestionnaire.pushByteS(cpu.getRegistres().getDP());
	                gestionnaire.pushByteS(cpu.getRegistres().getB());
	                gestionnaire.pushByteS(cpu.getRegistres().getA());
	                gestionnaire.pushByteS(cpu.getRegistres().getCC());
                }
                else {
                    // Si E était déjà à 1, empiler seulement PC et CC
                    gestionnaire.pushWordS(cpu.getRegistres().getPC());
                    gestionnaire.pushByteS(cpu.getRegistres().getCC());
                }
                
                int newCC = cpu.getRegistres().getCC() | 0x50; // Set E flag (bit 7) et I flag (bit 4)
                cpu.getRegistres().setCC(newCC);
                
                // Charger le vecteur d'interruption SWI depuis $FFFA-$FFFB
                cpu.getRegistres().setPC(mem.lireMot(0xFFFA));
                break;    
                
             // --- SYNC ---
            case 0x13:
                // SYNC: Attente d'interruption
                System.out.println("Attente d'interruption");
                break;
            
            // --- TFR (Transfer) ---
            case 0x1F:
                byte postByteTFR = (byte) mem.lire(cpu.getRegistres().getPC()); 
                cpu.getRegistres().setPC(cpu.getRegistres().getPC() + 1);
                gestionnaire.transfererRegistres(postByteTFR);
                break;
            
            // --- TST (Test) ---
            case 0x4D: gestionnaire.updateFlagsTst(cpu.getRegistres().getA()); break; // TSTA
            case 0x5D: gestionnaire.updateFlagsTst(cpu.getRegistres().getB()); break; // TSTB
            case 0x0D: gestionnaire.updateFlagsTst(mem.lire(adressage.getAdrsDirect())); break;
            case 0x6D: gestionnaire.updateFlagsTst(mem.lire(adressage.getAdrsIndexe())); break;
            case 0x7D: 
                adr = adressage.getAdrsEtendu(etenduIndirect);
                gestionnaire.updateFlagsTst(mem.lire(adr)); 
                break;
            
            default:
                System.out.printf("Opcode inconnu : %X%n", opcode);
                cpu.getRegistres().setPC(0xFFFF);
                break;
        }
    }
}