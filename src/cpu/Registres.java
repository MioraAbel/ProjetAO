package cpu;

public class Registres {
	//Registres de 8 bits
		protected int A;
		protected int B;   // Accumulateur B
		protected int DP;  // Direct Page
		protected int CC;  // Condition Codes

	    // --- Registres 16 bits ---
		protected int X;   // Index X
		protected int Y;   // Index Y
		protected int U;   // Stack pointer U
		protected int S;   // Stack pointer S
		protected int PC;  // Program Counter

	    // --- Constructeur des registres: tout à 0 ---
	    public Registres() {
			A = B = DP = CC = 0;
	        X = Y = U = S = PC = 0;
		}

	 // ----- Accesseurs / Modificateurs -----
		public int getA() {
			return A;
		}

		public void setA(int a) {
			A = a & 0xFF;
		}

		public int getB() {
			return B;
		}

		public void setB(int b) {
			B = b & 0xFF;
		}

		public int getDP() {
			return DP;
		}

		public void setDP(int dP) {
			DP = dP & 0xFF;
		}

		public int getCC() {
			return CC;
		}

		public void setCC(int cC) {
			CC = cC & 0xFF;
		}

		public int getX() {
			return X;
		}

		public void setX(int x) {
			X = x & 0xFFFF;
		}

		public int getY() {
			return Y;
		}

		public void setY(int y) {
			Y = y & 0xFFFF;
		}

		public int getU() {
			return U;
		}

		public void setU(int u) {
			U = u & 0xFFFF;
		}

		public int getS() {
			return S;
		}

		public void setS(int s) {
			S = s & 0xFFFF;
		}

		public int getPC() {
			return PC;
		}

		public void setPC(int pC) {
			PC = pC & 0xFFFF;
		}

}
