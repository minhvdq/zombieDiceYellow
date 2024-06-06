public class ZombieDice {
    //final double smallNumber = 1e-14;
    //final int maxScore = 20;

    //calculate pDiceRoll to get brain brains and sg shotguns out of dice dice left
    

    final int NUM_DICE = 3;
    final int MAX_SCORE = 30;
    final int WIN_SCORE = 13;
    public double[][][] pDiceRoll;
    public double[][] pExceed;
    public double[][][][][] pWin;

    //Constructor:
    public ZombieDice(){

        //pDiceRoll is the same as RollOutCome
        pDiceRoll = new double[NUM_DICE + 1][NUM_DICE + 1][NUM_DICE + 1];
        pExceed = new double[MAX_SCORE + 1][NUM_DICE];
        pWin = new double[2][MAX_SCORE][MAX_SCORE][MAX_SCORE][NUM_DICE];
        

        RollOutcomeProb rollOutComeProb = new RollOutcomeProb();

        pDiceRoll = rollOutComeProb.getRollProb();

        

        //initialize pExceed with -1
        for( int i = 0; i < MAX_SCORE; i ++){
            for( int j = 0; j < NUM_DICE; j ++){
                pExceed[i][j] = -1;
            }
        }
        

        //fill in pExceed
        for( int i  = 0; i < MAX_SCORE; i ++){
            for( int j = 0; j < NUM_DICE; j ++){
                if( i == 0){
                    pExceed[i][j] = 1.0;
                }
                else{
                    pExceed[i][j] = computepExceed( i, j);
                }
            }
        }
        
    }

    // calculate pExceed at scoreDiff and sRolled number of shotgun rolled
    public double computepExceed( int scoreDiff, int sRolled ){

        //there will be a chance where player keeps rolling all dice with footstep.
        // Deal with this case by excluding that case out of calculation which lead to the specical rollOutCome where NUMBER_ALL_OUTCOMES_ORDERED is less by one
        
        double[][][] pDiceRollSpecial = new RollOutcomeProb().getRollProb();
        if(sRolled >= 3){
            return 0.0;
        }
        if( scoreDiff < 0){
            return 1.0;
        }

        if( pExceed[scoreDiff][sRolled] == -1.0){
            pExceed[scoreDiff][sRolled] = 0;
            for( int sg = 0; sg <=3; sg ++){
                for( int b = 0; b <= 3 - sg; b ++){
                    //if( b != 0 || sg != 0){
                        int f = 3 - sg - b;
                        pExceed[scoreDiff][sRolled] += pDiceRollSpecial[3][b][sg] * computepExceed( scoreDiff - b, sRolled + sg);
                    
                    //}
                }
            }
        }
        return pExceed[scoreDiff][sRolled];
    }


    // This is my initial idea of calculating pExceed
 
    // public double computepExceed( int scoreDiff, int sRolled, double change ){
    //     if( change <= 1e-14){
    //         //there will be a chance where player keeps rolling all dice with footstep
    //         //set it to 1 if the probability of it go smaller than 1e-14( a super small
    //         //number )  to avoid infinite loop.
    //         return 1.0;
    //     }
    //     if(sRolled >= 3){
    //         return 0.0;
    //     }
    //     if( scoreDiff <= 0){
    //         return 1.0;
    //     }

    //     if( pExceed[scoreDiff][sRolled] == -1.0){
    //         pExceed[scoreDiff][sRolled] = 0;
    //         for( int sg = 0; sg <=3; sg ++){
    //             for( int b = 0; b <= 3 - sg; b ++){
    //                 int f = 3 - sg - b;
    //                 double curChange = pDiceRoll[3][b][sg] * change;
    //                 pExceed[scoreDiff][sRolled] += pDiceRoll[3][b][sg] * computepExceed( scoreDiff - b, sRolled + sg, curChange);
    //             }
    //         }
    //     }
    //     return pExceed[scoreDiff][sRolled];
    // }
    

    public void valueIterate( )
    {
        double maxChange = 0.0;

        do{
            maxChange = 0.0;
            for( int curPlayer = 0; curPlayer < 2; curPlayer ++){
                for( int curScore = 0; curScore < MAX_SCORE; curScore ++){
                    for( int opScore = 0; opScore < MAX_SCORE; opScore ++){
                        for( int turnTotal = 0; turnTotal < MAX_SCORE; turnTotal ++ ){
                            for( int  sg = 0; sg < NUM_DICE; sg  ++){
                                double oldValue = pWin[curPlayer][curScore][opScore][turnTotal][sg];
                                double pHold = 0;
                                if( turnTotal == 0){
                                    pHold = 0.0;
                                }
                                else if( curPlayer == 1 && curScore + turnTotal < opScore && opScore >= WIN_SCORE){
                                    pHold = 0.0;
                                }
                                else if( curPlayer == 1 && curScore + turnTotal > opScore && curScore + turnTotal >= WIN_SCORE){
                                    pHold = 1.0;
                                }
                                else{
                                    pHold = 1 - computeProbWin( 1 - curPlayer, opScore, curScore + turnTotal, 0, 0);
                                }
                                double pRoll = 0.0;
                                if( curPlayer == 1 && opScore >= WIN_SCORE && curScore + turnTotal < opScore){
                                    int scoreDiff = opScore - curScore - turnTotal;
                                    pRoll = pExceed[scoreDiff][sg];
                                }
                                else{
                                    for( int newSg = 0; newSg < NUM_DICE - sg; newSg ++){
                                        for( int newB = 0 ; newB <= NUM_DICE - newSg; newB ++){
                                            int f = 3 - newB - newSg;
                                            pRoll += pDiceRoll[3][newB][newSg] * computeProbWin( curPlayer, curScore, opScore, turnTotal + newB, sg + newSg);
                                        }
                                    }
                                    for( int newSg = 3 - sg; newSg <= NUM_DICE; newSg ++){
                                        for( int newB = 0 ; newB <= NUM_DICE - newSg; newB ++){
                                            int f = 3 - newB - newSg;
                                            pRoll += pDiceRoll[3][newB][newSg] * (1 - computeProbWin( 1 - curPlayer, opScore, curScore, 0, 0) );
                                        }
                                    }
                                }
                                pWin[curPlayer][curScore][opScore][turnTotal][sg] = pRoll >= pHold ? pRoll : pHold;
                                double curChange = Math.abs(oldValue - pWin[curPlayer][curScore][opScore][turnTotal][sg] );
                                maxChange = Math.max(maxChange, curChange);
                                //System.out.println(maxChange);
                            }
                        }
                    }
                }
            }
        }
        while(maxChange > 1e-14);
        System.out.println(maxChange);
    }

    public double computeProbWin( int curPlayer, int curScore, int opScore, int turnTotal, int sg ){
        if( curPlayer == 0 && curScore >= MAX_SCORE){
            return curScore >= opScore ? 1.0 : 0.0;
        }
        if( curPlayer == 1 && curScore + turnTotal >= WIN_SCORE && curScore + turnTotal > opScore){
            return 1.0;
        }
        if( curScore >= MAX_SCORE) curScore = MAX_SCORE - 1;
        if( opScore >= MAX_SCORE) opScore = MAX_SCORE - 1;
        if( curScore + turnTotal >= MAX_SCORE) turnTotal = MAX_SCORE - 1 - curScore;
        return pWin[curPlayer][curScore][opScore][turnTotal][sg];
    }

    public static void main(String[] args) {
        
        ZombieDice game = new ZombieDice();
        
        game.valueIterate();
        System.out.println("pWin[0][0][0][0][0]: " + game.pWin[0][0][0][0][0]);
        System.out.println("pWin[1][12][9][3][2]: " + game.pWin[1][12][9][3][2]);
        System.out.println("pExceed[0][2]: " + game.pExceed[0][2]);
        System.out.println("pDiceRoll[2][1][0]: " + game.pDiceRoll[2][1][0]);
        

    }
        
}