package asm;

import mem.Memoire;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.List;

public class Assembleur {
    
    public static int[] assemblerEtCharger(List<String> lignes, Memoire mem) {
        List<Byte> programme = new ArrayList<>();
        boolean erreurDetectee = false;
        
        for (String ligne : lignes) {
            ligne = ligne.trim();
            if (ligne.isEmpty() || ligne.equalsIgnoreCase("END")) {
                continue;
            }
            
            // Analyser l'instruction
            byte[] bytes = parseInstructionEnBytes(ligne);
            if (bytes != null && bytes.length > 0) {
                for (byte b : bytes) {
                    programme.add(b);
                }
            } else if (bytes != null && bytes.length == 0 && !ligne.isEmpty()) {
                // Une erreur syntaxique a été détectée
                erreurDetectee = true;
                break;
            }
        }
        
        if (erreurDetectee) {
            System.out.println("Erreur d'assemblage détectée. Programme non chargé.");
            return new int[]{0xFC00, 0xFC00};
        }
        
        // Convertir en tableau byte[]
        byte[] programmeBytes = new byte[programme.size()];
        for (int i = 0; i < programme.size(); i++) {
            programmeBytes[i] = programme.get(i);
        }
        
        // Charger dans la ROM à partir de 0xFC00
        int debutROM = 0xFC00;
        mem.chargerProgramme(debutROM, programmeBytes);
        
        System.out.println("Programme chargé dans la ROM (adresse " + 
                          String.format("$%04X", debutROM) + 
                          ", " + programmeBytes.length + " octets)");
        
        // Retourner [début, fin]
        int finROM = debutROM + programmeBytes.length;
        return new int[]{debutROM, finROM};
    }

    private static byte[] parseInstructionEnBytes(String ligne) {
        // Séparation mnémonique / opérande
        String[] parts = ligne.split("\\s+", 2);
        String mnemonic = parts[0].toUpperCase();
        String operand = (parts.length > 1) ? parts[1].trim() : "";
        
        List<Byte> bytes = new ArrayList<>();
        
        int opcode = 0;
        int postByte = -1;
        int offsetToWrite = 0;
        int offsetSize = 0; // 0=aucun, 1=byte, 2=word

        // --- MODE IMMEDIAT (#$xx) ---
        if (operand.startsWith("#")) {
            // Vérifier le format strict: #$ suivi de chiffres hexadécimaux
            if (!operand.matches("#\\$[0-9A-Fa-f]+")) {
                System.out.println("Erreur de syntaxe : format immédiat incorrect. Utilisez #$XX ou #$XXXX");
                return new byte[0]; // Tableau vide = pas d'instruction à charger
            }
            
            String valStr = operand.substring(2); // Retirer "#$"
            try {
                offsetToWrite = Integer.parseInt(valStr, 16);
            } catch (NumberFormatException e) {
                System.out.println("Erreur: Valeur hexadécimale invalide dans l'opérande");
                return new byte[0];
            }

            switch (mnemonic) {
                // Opérations 8 bits
            case "ADCA": opcode = 0x89; offsetSize = 1; break;
            case "ADCB": opcode = 0xC9; offsetSize = 1; break;
            case "ADDA": opcode = 0x8B; offsetSize = 1; break;
            case "ADDB": opcode = 0xCB; offsetSize = 1; break;
            case "ANDA": opcode = 0x84; offsetSize = 1; break;
            case "ANDB": opcode = 0xC4; offsetSize = 1; break;
            case "ANDCC":opcode = 0x1C; offsetSize = 1; break;
            case "BITA": opcode = 0x85; offsetSize = 1; break;
            case "BITB": opcode = 0xC5; offsetSize = 1; break;
            case "CMPA": opcode = 0x81; offsetSize = 1; break;
            case "CMPB": opcode = 0xC1; offsetSize = 1; break;
            case "CWAI": opcode = 0x3C; offsetSize = 1; break;
            case "EORA": opcode = 0x88; offsetSize = 1; break;
            case "EORB": opcode = 0xC8; offsetSize = 1; break;
            case "LDA":  opcode = 0x86; offsetSize = 1; break;
            case "LDB":  opcode = 0xC6; offsetSize = 1; break;
            case "ORA":  opcode = 0x8A; offsetSize = 1; break;
            case "ORB":  opcode = 0xCA; offsetSize = 1; break;
            case "ORCC": opcode = 0x1A; offsetSize = 1; break;
            case "SBCA": opcode = 0x82; offsetSize = 1; break;
            case "SBCB": opcode = 0xC2; offsetSize = 1; break;
            case "SUBA": opcode = 0x80; offsetSize = 1; break;
            case "SUBB": opcode = 0xC0; offsetSize = 1; break;

            // Opérations 16 bits
            case "ADDD": opcode = 0xC3; offsetSize = 2; break;
            case "CMPD": opcode = 0x1083; offsetSize = 2; break;
            case "CMPX": opcode = 0x8C; offsetSize = 2; break;
            case "CMPY": opcode = 0x108C; offsetSize = 2; break;
            case "CMPU": opcode = 0x1183; offsetSize = 2; break;
            case "CMPS": opcode = 0x118C; offsetSize = 2; break;
            case "LDD":  opcode = 0xCC; offsetSize = 2; break;
            case "LDS":  opcode = 0x10CE; offsetSize = 2; break;
            case "LDU":  opcode = 0xCE; offsetSize = 2; break;
            case "LDX":  opcode = 0x8E; offsetSize = 2; break;
            case "LDY":  opcode = 0x108E; offsetSize = 2; break;
            case "SUBD": opcode = 0x83; offsetSize = 2; break;

            // Spéciaux (Registres ou Piles)
            case "EXG":  opcode = 0x1E; offsetSize = 1; break;
            case "TFR":  opcode = 0x1F; offsetSize = 1; break;
            case "PSHS": opcode = 0x34; offsetSize = 1; break;
            case "PULS": opcode = 0x35; offsetSize = 1; break;
            case "PSHU": opcode = 0x36; offsetSize = 1; break;
            case "PULU": opcode = 0x37; offsetSize = 1; break;
            
            default:
                System.out.println("Erreur: Instruction " + mnemonic + " non supportée en mode immédiat.");
                return new byte[0]; // Retourne tableau vide en cas d'erreur
            }

            // VÉRIFICATION DE LA TAILLE DE L'OPÉRANDE
            if (offsetSize == 1) {
                // Pour les registres 8 bits, l'opérande doit tenir sur 1 octet
                if (offsetToWrite > 0xFF) {
                    System.out.println("Erreur: L'opérande #$" + Integer.toHexString(offsetToWrite).toUpperCase() + 
                                     " dépasse la taille d'un octet pour l'instruction " + mnemonic);
                    return new byte[0];
                }
            } else if (offsetSize == 2) {
                // Pour les registres 16 bits, l'opérande doit tenir sur 2 octets
                if (offsetToWrite > 0xFFFF) {
                    System.out.println("Erreur: L'opérande #$" + Integer.toHexString(offsetToWrite).toUpperCase() + 
                                     " dépasse la taille d'un mot (16 bits) pour l'instruction " + mnemonic);
                    return new byte[0];
                }
            }
        }
        
        // --- MODE DIRECT ($xx) ---
        else if (operand.matches("\\$[0-9A-Fa-f]{2}")) {
            String hexStr = operand.substring(1);
            offsetToWrite = Integer.parseInt(hexStr, 16);
            offsetToWrite = Integer.parseInt(operand.substring(1), 16);
            
            if (offsetToWrite > 0xFF) {
                System.out.println("Attention: pour le mode direct, l'adresse $" + 
                                  hexStr + " sera tronquée à $" + 
                                  String.format("%02X", offsetToWrite & 0xFF));
            }
            offsetToWrite = offsetToWrite & 0xFF;
            offsetSize = 1;

            switch (mnemonic) {
                case "ADCA": opcode = 0x99; break;
                case "ADCB": opcode = 0xD9; break;
                case "ADDA": opcode = 0x9B; break;
                case "ADDB": opcode = 0xDB; break;
                case "ADDD": opcode = 0xD3; break;
                case "ANDA": opcode = 0x94; break;
                case "ANDB": opcode = 0xD4; break;
                case "ASL":  opcode = 0x08; break;
                case "ASR":  opcode = 0x07; break;
                case "BITA": opcode = 0x95; break;
                case "BITB": opcode = 0xD5; break;
                case "CLR":  opcode = 0x0F; break;
                case "CMPA": opcode = 0x91; break;
                case "CMPB": opcode = 0xD1; break;
                case "CMPD": opcode = 0x1093; break;
                case "CMPX": opcode = 0x9C; break;
                case "CMPY": opcode = 0x109C; break;
                case "CMPS": opcode = 0x119C; break;
                case "CMPU": opcode = 0x1193; break;
                case "COM":  opcode = 0x03; break;
                case "DEC":  opcode = 0x0A; break;
                case "EORA": opcode = 0x98; break;
                case "EORB": opcode = 0xD8; break;
                case "INC":  opcode = 0x0C; break;
                case "JMP":  opcode = 0x0E; break;
                case "JSR":  opcode = 0x9D; break;
                case "LDA":  opcode = 0x96; break;
                case "LDB":  opcode = 0xD6; break;
                case "LDD":  opcode = 0xDC; break;
                case "LDS":  opcode = 0x10DE; break;
                case "LDU":  opcode = 0xDE; break;
                case "LDX":  opcode = 0x9E; break;
                case "LDY":  opcode = 0x109E; break;
                case "LSL":  opcode = 0x08; break;
                case "LSR":  opcode = 0x04; break;
                case "NEG":  opcode = 0x00; break;
                case "ORA":  opcode = 0x9A; break;
                case "ORB":  opcode = 0xDA; break;
                case "ROL":  opcode = 0x09; break;
                case "ROR":  opcode = 0x06; break;
                case "SBCA": opcode = 0x92; break;
                case "SBCB": opcode = 0xD2; break;
                case "STA":  opcode = 0x97; break;
                case "STB":  opcode = 0xD7; break;
                case "STD":  opcode = 0xDD; break;
                case "STS":  opcode = 0x109F; break;
                case "STU":  opcode = 0xDF; break;
                case "STX":  opcode = 0x9F; break;
                case "STY":  opcode = 0x109F; break;
                case "SUBA": opcode = 0x90; break;
                case "SUBB": opcode = 0xD0; break;
                case "SUBD": opcode = 0x93; break;
                case "TST":  opcode = 0x0D; break;
                
                default:
                    System.out.println("Erreur: Instruction " + mnemonic + " non supportée en mode direct.");
                    return new byte[0];
            }
        }
        
        // --- MODE ETENDU DIRECT OU INDIRECT ($xxxx ou [$xxxx]) ---
        else if (operand.matches("\\[\\$[0-9A-Fa-f]{4}\\]") || operand.matches("\\$[0-9A-Fa-f]{4}")) {
            boolean indirect = operand.startsWith("[");
            String hexStr;
            
            if (indirect) {
                // Retirer les crochets pour [$xxxx]
                hexStr = operand.substring(2, operand.length() - 1);
            } else {
                // Retirer le $ pour $xxxx
                hexStr = operand.substring(1);
            }
            
            offsetToWrite = Integer.parseInt(hexStr, 16);
            offsetSize = 2;

            switch (mnemonic) {
                case "ADCA": opcode = 0xB9; break;
                case "ADCB": opcode = 0xF9; break;
                case "ADDA": opcode = 0xBB; break;
                case "ADDB": opcode = 0xFB; break;
                case "ADDD": opcode = 0xF3; break;
                case "ANDA": opcode = 0xB4; break;
                case "ANDB": opcode = 0xF4; break;
                case "ASL":  opcode = 0x78; break;
                case "ASR":  opcode = 0x77; break;
                case "BITA": opcode = 0xB5; break;
                case "BITB": opcode = 0xF5; break;
                case "CLR":  opcode = 0x7F; break;
                case "CMPA": opcode = 0xB1; break;
                case "CMPB": opcode = 0xF1; break;
                case "CMPD": opcode = 0x10B3; break;
                case "CMPX": opcode = 0xBC; break;
                case "CMPY": opcode = 0x10BC; break;
                case "CMPS": opcode = 0x11BC; break;
                case "CMPU": opcode = 0x11B3; break;
                case "COM":  opcode = 0x73; break;
                case "DEC":  opcode = 0x7A; break;
                case "EORA": opcode = 0xB8; break;
                case "EORB": opcode = 0xF8; break;
                case "INC":  opcode = 0x7C; break;
                case "JMP":  opcode = 0x7E; break;
                case "JSR":  opcode = 0xBD; break;
                case "LDA":  opcode = 0xB6; break;
                case "LDB":  opcode = 0xF6; break;
                case "LDD":  opcode = 0xFC; break;
                case "LDS":  opcode = 0x10FE; break;
                case "LDU":  opcode = 0xFE; break;
                case "LDX":  opcode = 0xBE; break;
                case "LDY":  opcode = 0x10BE; break;
                case "LSL":  opcode = 0x78; break;
                case "LSR":  opcode = 0x74; break;
                case "NEG":  opcode = 0x70; break;
                case "ORA":  opcode = 0xBA; break;
                case "ORB":  opcode = 0xFA; break;
                case "ROL":  opcode = 0x79; break;
                case "ROR":  opcode = 0x76; break;
                case "SBCA": opcode = 0xB2; break;
                case "SBCB": opcode = 0xF2; break;
                case "STA":  opcode = 0xB7; break;
                case "STB":  opcode = 0xF7; break;
                case "STD":  opcode = 0xFD; break;
                case "STS":  opcode = 0x10FF; break;
                case "STU":  opcode = 0xFF; break;
                case "STX":  opcode = 0xBF; break;
                case "STY":  opcode = 0x10BF; break;
                case "SUBA": opcode = 0xB0; break;
                case "SUBB": opcode = 0xF0; break;
                case "SUBD": opcode = 0xB3; break;
                case "TST":  opcode = 0x7D; break;
                
                default:
                    System.out.println("Erreur: Instruction " + mnemonic + " non supportée en mode étendu.");
                    return new byte[0];
            }
        }
        
     // --- MODE INHERENT (Pas d'opérande) ---
        else if (operand.isEmpty()) {
            switch (mnemonic) {
                case "ABX":  opcode = 0x3A; break;
                case "ASLA": opcode = 0x48; break;
                case "ASLB": opcode = 0x58; break;
                case "CLRA": opcode = 0x4F; break;
                case "CLRB": opcode = 0x5F; break;
                case "COMA": opcode = 0x43; break;
                case "COMB": opcode = 0x53; break;
                case "DAA":  opcode = 0x19; break;
                case "DECA": opcode = 0x4A; break;
                case "DECB": opcode = 0x5A; break;
                case "INCA": opcode = 0x4C; break;
                case "INCB": opcode = 0x5C; break;
                case "LSLA": opcode = 0x48; break;
                case "LSLB": opcode = 0x58; break;
                case "LSRA": opcode = 0x44; break;
                case "LSRB": opcode = 0x54; break;
                case "MUL":  opcode = 0x3D; break;
                case "NEGA": opcode = 0x40; break;
                case "NEGB": opcode = 0x50; break;
                case "NOP":  opcode = 0x12; break;
                case "ROLA": opcode = 0x49; break;
                case "ROLB": opcode = 0x59; break;
                case "RORA": opcode = 0x46; break;
                case "RORB": opcode = 0x56; break;
                case "RTI": opcode = 0x3B; break;
                case "RTS":  opcode = 0x39; break;
                case "SEX":  opcode = 0x1D; break;
                case "SYNC": opcode = 0x13; break;
                case "SWI": opcode = 0x3F; break;
                case "TSTA": opcode = 0x4D; break;
                case "TSTB": opcode = 0x5D; break;
                
                default: 
                    System.out.println("Erreur: Mnémonique " + mnemonic + " non supportée en mode inhérent.");
                    return new byte[0];
            }
        }
        
        // --- MODE INDEXÉ (DIRECT ET INDIRECT) ---
        else if ( operand.contains(",") || ( operand.startsWith("[") && operand.endsWith("]") ) ) {
        	switch (mnemonic) {
	            case "ADCA": opcode = 0xA9; break;
	            case "ADCB": opcode = 0xE9; break;
	            case "ADDA": opcode = 0xAB; break;
	            case "ADDB": opcode = 0xEB; break;
	            case "ADDD": opcode = 0xE3; break;
	            case "ANDA": opcode = 0xA4; break;
	            case "ANDB": opcode = 0xE4; break;
	            case "ASL":  opcode = 0x68; break;
	            case "ASR":  opcode = 0x67; break;
	            case "BITA": opcode = 0xA5; break;
	            case "BITB": opcode = 0xE5; break;
	            case "CLR":  opcode = 0x6F; break;
	            case "CMPA": opcode = 0xA1; break;
	            case "CMPB": opcode = 0xE1; break;
	            case "CMPD": opcode = 0x10A3; break;
	            case "CMPX": opcode = 0xAC; break;
	            case "CMPY": opcode = 0x10AC; break;
	            case "CMPS": opcode = 0x11AC; break;
	            case "CMPU": opcode = 0x11A3; break;
	            case "COM":  opcode = 0x63; break;
	            case "DEC":  opcode = 0x6A; break;
	            case "EORA": opcode = 0xA8; break;
	            case "EORB": opcode = 0xE8; break;
	            case "INC":  opcode = 0x6C; break;
	            case "JMP":  opcode = 0x6E; break;
	            case "JSR":  opcode = 0xAD; break;
	            case "LDA":  opcode = 0xA6; break;
	            case "LDB":  opcode = 0xE6; break;
	            case "LDD":  opcode = 0xEC; break;
	            case "LDS":  opcode = 0x10EE; break;
	            case "LDU":  opcode = 0xEE; break;
	            case "LDX":  opcode = 0xAE; break;
	            case "LDY":  opcode = 0x10AE; break;
	            case "LEAS": opcode = 0x32; break; // Indexé seulement
	            case "LEAU": opcode = 0x33; break; // Indexé seulement
	            case "LEAX": opcode = 0x30; break; // Indexé seulement
	            case "LEAY": opcode = 0x31; break; // Indexé seulement
	            case "LSL":  opcode = 0x68; break;
	            case "LSR":  opcode = 0x64; break;
	            case "NEG":  opcode = 0x60; break;
	            case "ORA":  opcode = 0xAA; break;
	            case "ORB":  opcode = 0xEA; break;
	            case "ROL":  opcode = 0x69; break;
	            case "ROR":  opcode = 0x66; break;
	            case "SBCA": opcode = 0xA2; break;
	            case "SBCB": opcode = 0xE2; break;
	            case "STA":  opcode = 0xA7; break;
	            case "STB":  opcode = 0xE7; break;
	            case "STD":  opcode = 0xED; break;
	            case "STS":  opcode = 0x10EF; break;
	            case "STU":  opcode = 0xEF; break;
	            case "STX":  opcode = 0xAF; break;
	            case "STY":  opcode = 0x10AF; break;
	            case "SUBA": opcode = 0xA0; break;
	            case "SUBB": opcode = 0xE0; break;
	            case "SUBD": opcode = 0xA3; break;
	            case "TST":  opcode = 0x6D; break;
	            
	            default:
	                System.out.println("Erreur: Instruction " + mnemonic + " non supportée en mode indexé.");
	                return new byte[0];
	        }
        


        	// --- CALCUL DU POST-BYTE ---
        boolean isIndirect = operand.startsWith("[") && operand.endsWith("]");
        String content = isIndirect ? operand.substring(1, operand.length()-1).trim() : operand.trim();
        //if (!content.contains(",")) content = "0," + content;

        if (!content.contains(",")) {
            System.out.println("Erreur: Format indexé incorrect");
            return new byte[0];
        }
        
        String[] idxParts = content.split(",");
        if (idxParts.length != 2) {
            System.out.println("Erreur: Format indexé incorrect");
            return new byte[0];
        }
        
        String offsetPart = idxParts[0].trim();
        String basePart = idxParts[1].trim();

        Pattern basePattern = Pattern.compile("^([-+]{0,2})?([XYUS])([-+]{0,2})?$", Pattern.CASE_INSENSITIVE);
        Matcher m = basePattern.matcher(basePart);

        if (m.matches()) {
            String pre = m.group(1) != null ? m.group(1) : "";
            String reg = m.group(2).toUpperCase();
            String post = m.group(3) != null ? m.group(3) : "";
            
            int rr = 0;
            if (reg.equals("X")) rr = 0; 
            else if (reg.equals("Y")) rr = 1;
            else if (reg.equals("U")) rr = 2; 
            else if (reg.equals("S")) rr = 3;
            else if (reg.equals("PC")) rr = 5;

            // CAS 1: Auto Incrément/Décrément
            if (!pre.isEmpty() || !post.isEmpty()) {
                int modeNibble = -1;
                
             // Post-incrémentation (R+, R++)
                if (post.equals("+")) modeNibble = 0x0;
                else if (post.equals("++")) modeNibble = 0x1;
                // Pré-incrémentation (+R, ++R)
                else if (pre.equals("+")) modeNibble = 0x0; // +R équivaut à ,R+
                else if (pre.equals("++")) modeNibble = 0x1; // ++R équivaut à ,R++
                // Pré-décrémentation (-R, --R)
                else if (pre.equals("-")) modeNibble = 0x2;
                else if (pre.equals("--")) modeNibble = 0x3;
                // Post-décrémentation (R-, R--) - non standard mais pourrait être géré
                else if (post.equals("-")) modeNibble = 0x2; // R- équivaut à ,-R
                else if (post.equals("--")) modeNibble = 0x3; // R-- équivaut à ,--R
                
                if (modeNibble != -1) {
                    postByte = 0x80 | (rr << 5) | modeNibble;
                    if (isIndirect) postByte |= 0x10;
                }
            }
            
         // CAS 2: Pas d'auto-incrémentation (,R)
            else if (offsetPart.isEmpty()) {
                postByte = 0x80 | (rr << 5) | 0x04;
                if (isIndirect) postByte |= 0x10;
            }
            
            // CAS 3: Offset
            else {
                int val = 0;
                boolean isRegisterOffset = false;
                
                if (offsetPart.matches("[ABDabd]")) {
                	String acc = offsetPart.toUpperCase();
                    int accBit = 0;
                    if (acc.equals("A")) accBit = 0x6;
                    else if (acc.equals("B")) accBit = 0x5;
                    else if (acc.equals("D")) accBit = 0xB;
                    
                    postByte = 0x80 | (rr << 5) | accBit;
                    isRegisterOffset = true;
                } 
                
                else if (reg.equals("PC")) {
                    if (offsetPart.startsWith("$")) {
                        val = Integer.parseInt(offsetPart.substring(1), 16);
                    } else {
                        val = Integer.parseInt(offsetPart);
                    }
                    
                    if (val >= -128 && val <= 127) {
                        postByte = 0x80 | (rr << 5) | 0x0C; // PC avec offset 8 bits
                        offsetToWrite = val & 0xFF;
                        offsetSize = 1;
                    } else {
                        postByte = 0x80 | (rr << 5) | 0x0D; // PC avec offset 16 bits
                        offsetToWrite = val & 0xFFFF;
                        offsetSize = 2;
                    }
                } 
                
                else {
                	if (offsetPart.startsWith("$")) {
                        val = Integer.parseInt(offsetPart.substring(1), 16);
                    } else {
                        // Pour les nombres signés décimaux
                        try {
                            val = Integer.parseInt(offsetPart);
                        } catch (NumberFormatException e) {
                            System.out.println("Erreur: Offset non valide: " + offsetPart);
                            return new byte[0];
                        }
                    }
                    
                    // Mode 5-bit offset (sans bit 0x80)
                    if (!isIndirect && val >= -16 && val <= 15) {
                        postByte = (rr << 5) | (val & 0x1F);
                    } 
                    // Offset 8 bits
                    else if (val >= -128 && val <= 127) {
                        postByte = 0x80 | (rr << 5) | 0x08;
                        offsetToWrite = val & 0xFF;
                        offsetSize = 1;
                    } 
                    // Offset 16 bits
                    else {
                        postByte = 0x80 | (rr << 5) | 0x09;
                        offsetToWrite = val & 0xFFFF;
                        offsetSize = 2;
                    }
                }
                
                if (isIndirect && !isRegisterOffset) {
                    postByte |= 0x10;
                }
            }
        } else { 
            System.out.println("Erreur Syntaxe Indexée");
            return new byte[0];
        }
        }
        

        else {
            System.out.println("Erreur: Opérande non reconnue: " + operand);
            return new byte[0];
        }

        
        if (opcode != 0) {
            if (opcode > 0xFF) {
                bytes.add((byte)(opcode >> 8));
                bytes.add((byte)(opcode & 0xFF));
            } else {
                bytes.add((byte)opcode);
            }

            if (postByte != -1) {
                bytes.add((byte)postByte);
            }

            if (offsetSize == 1) {
                bytes.add((byte)offsetToWrite);
            } else if (offsetSize == 2) {
                bytes.add((byte)(offsetToWrite >> 8));
                bytes.add((byte)(offsetToWrite & 0xFF));
            }

            System.out.printf("-> Instruction '%s %s' -> %d octet(s): ", 
                    mnemonic, operand, bytes.size());
            
            // Afficher les octets en hexa
            for (Byte b : bytes) {
                System.out.printf("%02X ", b & 0xFF);
            }
            System.out.println();
        } else {
            return new byte[0];
        	}

        // Convertir en tableau
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }

        return result;
    }
    
 // Helper pour parser les registres des instructions spéciales (EXG A,B -> postbyte)
    private static int parseSpecialOp(String mnemonic, String op) {
        if (mnemonic.startsWith("PSH") || mnemonic.startsWith("PUL")) {
            // Conversion simple liste -> byte (Ex: PSHS A,B,X -> PC=bit7, U=6, Y=5, X=4, DP=3, B=2, A=1, CC=0)
            int mask = 0;
            String[] regs = op.split(",");
            for(String r : regs) {
                r=r.trim().toUpperCase();
                if(r.equals("PC")) mask|=0x80; 
                else if(r.equals("U") || r.equals("S")) mask|=0x40;
                else if(r.equals("Y")) mask|=0x20;
                else if(r.equals("X")) mask|=0x10;
                else if(r.equals("DP")) mask|=0x08;
                else if(r.equals("B")) mask|=0x04;
                else if(r.equals("A")) mask|=0x02;
                else if(r.equals("CC")) mask|=0x01;
            }
            return mask;
        }
        return -1;
    }

    private static int getRegCode(String r) {
        r = r.trim().toUpperCase();
        if(r.equals("D")) return 0; if(r.equals("X")) return 1; if(r.equals("Y")) return 2;
        if(r.equals("U")) return 3; if(r.equals("S")) return 4; if(r.equals("PC")) return 5;
        if(r.equals("A")) return 8; if(r.equals("B")) return 9; if(r.equals("CC")) return 10;
        if(r.equals("DP")) return 11; 
        return 0;
    }
}