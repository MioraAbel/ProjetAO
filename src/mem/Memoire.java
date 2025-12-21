package mem;

import java.util.ArrayList;
import java.util.List;

public class Memoire {
	 private byte[] mem;
	 private boolean[] isROM;
	 
	 private List<MemoryListener> listeners = new ArrayList<>();
	 
	 //Constructeur de la mémoire de sorte à réserver une bloc d'adresse pour le stockage des programmes seulement
	 public Memoire()
	 {
		 mem = new byte[65536];
		 isROM = new boolean[65536];
		 for (int addr = 0xFC00; addr <= 0xFFFF; addr++) {
	            isROM[addr] = true;
        }
	 }
	 
	 public void addListener(MemoryListener listener) {
	        listeners.add(listener);
	    }

	    public void removeListener(MemoryListener listener) {
	        listeners.remove(listener);
	    }

	    private void notifyChange(int address, int value) {
	        for (MemoryListener l : listeners) {
	            l.memoryChanged(address & 0xFFFF, value);
	        }
	    }

	 
	 //Lire un octet à une adresse donnée
	 public int lire(int adresse)
	 {
		 return mem[adresse & 0xFFFF] & 0xFF ;
	 }
	 
	 //Ecriture autorisé sur RAM (mémoire des données) seulement
	 public void ecrire(int adresse, int valeur)
	 {
		 adresse &= 0xFFFF;
		 if (isROM[adresse]) {
            System.err.println(String.format(
                "Erreur : tentative d'écriture en ROM à l'adresse %04X", adresse));
            return;}
		 mem[adresse]=(byte) valeur;
	     notifyChange(adresse, valeur);
	 }
	 
	 public int lireMot(int adresse)
	 {
		 int haut = mem[adresse & 0xFFFF] & 0xFF;
		 int bas = mem[(adresse+1) & 0xFFFF] & 0xFF;
		 return (haut <<8)|bas;
	 }
	 
	// Écrire un mot (16 bits). SUR RAM SEULEMENTS
	  public void ecrireMot(int adresse, int valeur) {
		 if (isROM[adresse]) {
			 System.err.println(String.format ("Erreur : tentative d'écriture en ROM à l'adresse %04X", adresse));
			 return;
		 }
		 
		 byte haut=(byte)((valeur >> 8) & 0xFF);
		 byte bas=(byte)(valeur & 0xFF);
		 
	     mem[adresse & 0xFFFF] = haut;
	     mem[(adresse + 1) & 0xFFFF] = bas;
	     

        notifyChange(adresse, haut);
        notifyChange((adresse + 1) & 0xFFFF, bas);
	  }
	  
	//CHARGER UN PROGRAMME DANS ROM
	  //FORCER L'ECRITURE DES PROGRAMMMES DANS ROM
    public void ecrireForce(int address, byte value) {
        address &= 0xFFFF;
        mem[address] = value;
        notifyChange(address, value);
    }

    //CHARGEMENT EN INDIQUANT L'ADRESSE DU DEBUT
    public void chargerProgramme(int startAddr, byte[] programme) {
        for (int i = 0; i < programme.length; i++) {
            int addr = (startAddr + i) & 0xFFFF;
            ecrireForce(addr, programme[i]);
        }
    }
    
    //EFFACER LA MÉMOIRE (RESET)
    public void clearRAM() {
        for (int addr = 0; addr < 0xFC00; addr++) {
            mem[addr] = 0;
            notifyChange(addr, (byte)0);
        }
    }
    public int concat(int ad1, int ad2){
		  return (ad1 <<8)|ad2;
	}
}
