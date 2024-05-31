public class RollOutcomeProb {
    final int NUM_DICE = 3;
    final int NUM_FACE = 3;
    int NUM_OUT_CASES = (int) Math.pow(NUM_FACE, NUM_DICE);  
    int[][] choose = new int[NUM_DICE + 1][NUM_DICE + 1 ];
    double[][][] rollProb = new double[NUM_DICE + 1][NUM_DICE + 1][NUM_DICE + 1];
    double totalProb = 0;

    public RollOutcomeProb(){

    }

    public RollOutcomeProb( boolean check ){
        if( check == true ){
            NUM_OUT_CASES -= 1;
        }
    }

    private void calChoose() {
        for(int d = 0; d <= NUM_DICE; d ++ ){
            for( int b = 0; b <= NUM_DICE; b ++ ){
                if(b == 0){
                    choose[d][b] = 1;
                }
                else if( d == 0 ){
                    choose[d][b] = 0;
                }else{
                    choose[d][b] = choose[d - 1][b - 1] + choose[d - 1][b];
                }
            }
        }
    }

    public void calProb() {
        calChoose();
        for( int d = 0; d <= NUM_DICE; d ++ ){
            for( int b = 0; b <= d; b ++ ){
                for( int s = 0; s <= d-b; s ++){
                    int f = d - b - s;
                    int cases = choose[d][b] * choose[d - b][s];
                    double prob = (double) cases/NUM_OUT_CASES;
                    System.out.printf("b=%d, f=%d, s=%s: %d of %d outcomes (probability %f)\n", 
							b, f, s, cases, NUM_OUT_CASES, prob);
                            
                    if( d == NUM_DICE ){
                        totalProb += prob;
                    }
                    rollProb[d][b][s] = prob;

                    
                }
            }
        }
        System.out.println("Total probability is " + totalProb);
    }

    public double[][][] getRollProb () {
        calProb();
        return rollProb;
    }

    public static void main( String[] args){
        RollOutcomeProb outcomeProb = new RollOutcomeProb();
        outcomeProb.calProb();
    }
}
