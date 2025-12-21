package main;

import cpu.CPU6809;
import mem.Memoire;
import ui.Console;
import asm.Assembleur;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //Initialisation - NE PAS créer une nouvelle Memoire ici !
        CPU6809 cpu = new CPU6809();
        Console console = new Console();
        
        // Récupérer la mémoire du CPU (celle qui sera utilisée)
        Memoire mem = cpu.getMemory();
        
        //Lecture du programme
        List<String> lignes = console.lireProgramme();
        
        //Assemblage et chargement dans la mémoire du CPU
        int[] adresses = Assembleur.assemblerEtCharger(lignes, mem);
        int debutProgramme = adresses[0];
        int finProgramme = adresses[1];
        
     // Vérifier si le programme a été chargé (début != fin)
       if (debutProgramme == finProgramme) {
            console.close();
            return;
        }
        
        cpu.getRegistres().setPC(debutProgramme); // PC = 0xFC00
        
        System.out.println("\n===========================================================\n");
        
        System.out.printf("\n-------- EXECUTION (début à $%04X, fin à $%04X) --------%n", 
                         debutProgramme, finProgramme-1);
        
        while (cpu.getRegistres().getPC() < finProgramme) {
            cpu.runCycle();
        }
        
        //Affichage final
        System.out.printf("\n-------- REGISTRES FINAUX --------%n");
        console.afficherRegistres(cpu, mem);
        
        //Fermeture
        console.close();
    }
}