package ui;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import asm.Assembleur;
import mem.Memoire;
import cpu.CPU6809;

public class Console {
    private Scanner scanner;
    
    public Console() {
        scanner = new Scanner(System.in);
    }
    
    public List<String> lireProgramme() {
        List<String> lignes = new ArrayList<>();
        System.out.println("Entrer votre programme assembleur (Taper END pour finir)");
        
        while (true) {
            String ligne = scanner.nextLine().trim();
            if (ligne.equalsIgnoreCase("END")) break;
            if (ligne.isEmpty()) continue;
            lignes.add(ligne);
        }
        
        return lignes;
    }
    
    public void afficherRegistres(CPU6809 cpu, Memoire mem) {
        System.out.println("A=" + String.format("%02X", cpu.getRegistres().getA()) +
                         " B=" + String.format("%02X", cpu.getRegistres().getB()) +
                         " D=" + String.format("%04X", mem.concat(cpu.getRegistres().getA(), cpu.getRegistres().getB())) +
                         " X=" + String.format("%04X", cpu.getRegistres().getX()) +
                         " Y=" + String.format("%04X", cpu.getRegistres().getY()) +
                         " U=" + String.format("%04X", cpu.getRegistres().getU()) +
                         " S=" + String.format("%04X", cpu.getRegistres().getS()) +
                         " PC=" + String.format("%04X", cpu.getRegistres().getPC()));
    }
    
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}