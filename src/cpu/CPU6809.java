package cpu;

import exec.UniteExecution;
import mem.Memoire;

public class CPU6809 {
	private Registres registre;
	private Memoire memoire;
	private UniteExecution decoder;
	private int RI;

	public CPU6809( ) {
		this.registre = new Registres();
		this.memoire = new Memoire();
		this.decoder = new UniteExecution(this, this.memoire);
		this.RI = 0;
		
		//On va initialiser le registre S et U
		this.registre.setS(0x1000);
		this.registre.setU(0x2000);
		
		//Initialiser le vecteur SWI sur une adresse sécurisée
		this.memoire.ecrireForce(0xFFFA, (byte)0xF0);
		this.memoire.ecrireForce(0xFFFB, (byte)0x00);  // SWI vector -> $F000
	}
	
    public void runCycle() {
    	int opcode = fetch();
    	 //System.out.printf("PC=%04X, Opcode=%02X%n", getRegistres().getPC()-1, opcode & 0xFF);
        decoder.executerInstruction(opcode);
    }
	
	public int fetch() {
		int pc = registre.getPC();
	    int firstByte = memoire.lire(pc & 0xFFFF);
	    int opcode;
	    
	    if (firstByte == 0x10 || firstByte == 0x11) {
	        registre.setPC((pc + 1) & 0xFFFF); // Avancer PC pour lire le deuxième octet
	        int secondByte = memoire.lire(registre.getPC()) & 0xFF;
	        opcode = (firstByte << 8) | secondByte; // Opcode sur 16 bits
            registre.setPC((registre.getPC() + 1) & 0xFFFF);
        } else {
        	opcode = firstByte;
        	registre.setPC((pc + 1) & 0xFFFF);
        }
	    
	    this.RI = opcode;

	    return opcode;
	}
	
	//ACCES MEMOIRE
	 public Memoire getMemory() {
	        return memoire;
	    }
	 
	 public Registres getRegistres() {
		 return registre;
	 }

	
}
