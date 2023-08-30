public class Poketudiant {
    private String variety;
    private String type;
    private int lvl;
    private int hp;
    private int hpMax;
    private int exp;
    private int expMax;
    private int attack;
    private int defence;
    private String attack1Name;
    private String attack1Type;
    private String attack2Type;
    private String attack2Name;

        /**
     * @param variety
     * @param type
     * @param lvl
     * @param hp
     * @param hpMax
     * @param exp
     * @param expMax
     * @param attack
     * @param defence
     * @param attack1Name
     * @param attack1Type
     * @param attack2Type
     * @param attack2Name
     */
    public Poketudiant(String variety, String type, int lvl, int exp, int expMax, int hp, int hpMax, int attack,
            int defence, String attack1Name, String attack1Type, String attack2Type, String attack2Name) {
        this.variety = variety;
        this.type = type;
        this.lvl = lvl;
        this.hp = hp;
        this.hpMax = hpMax;
        this.exp = exp;
        this.expMax = expMax;
        this.attack = attack;
        this.defence = defence;
        this.attack1Name = attack1Name;
        this.attack1Type = attack1Type;
        this.attack2Type = attack2Type;
        this.attack2Name = attack2Name;
    }

    

        /**
         * @return the variety
         */
        public String getVariety() {
            return variety;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @return the lvl
         */
        public int getLvl() {
            return lvl;
        }

        /**
         * @return the hp
         */
        public int getHp() {
            return hp;
        }

        /**
         * @return the hpMax
         */
        public int getHpMax() {
            return hpMax;
        }

        /**
         * @return the exp
         */
        public int getExp() {
            return exp;
        }

        /**
         * @return the expMax
         */
        public int getExpMax() {
            return expMax;
        }

        /**
         * @return the attack
         */
        public int getAttack() {
            return attack;
        }

        /**
         * @return the defence
         */
        public int getDefence() {
            return defence;
        }

        /**
         * @return the attack1Name
         */
        public String getAttack1Name() {
            return attack1Name;
        }

        /**
         * @return the attack1Type
         */
        public String getAttack1Type() {
            return attack1Type;
        }

        /**
         * @return the attack2Type
         */
        public String getAttack2Type() {
            return attack2Type;
        }

        /**
         * @return the attack2Name
         */
        public String getAttack2Name() {
            return attack2Name;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        
        @Override
        public String toString() {
            return "Poketudiant [attack=" + attack + ", attack1Name=" + attack1Name + ", attack1Type=" + attack1Type
                    + ", attack2Name=" + attack2Name + ", attack2Type=" + attack2Type + ", defence=" + defence
                    + ", exp=" + exp + ", expMax=" + expMax + ", hp=" + hp + ", hpMax=" + hpMax + ", lvl=" + lvl
                    + ", type=" + type + ", variety=" + variety + "]";
        }
}
