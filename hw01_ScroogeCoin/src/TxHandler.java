import java.util.ArrayList;


public class TxHandler {


    private UTXOPool utxoPool;
    private final int txMaxSize = 100;
    private Transaction[] txArrays;

	/* Creates a public ledger whose current UTXOPool (collection of unspent 
	 * transaction outputs) is utxoPool. This should make a defensive copy of 
	 * utxoPool by using the UTXOPool(UTXOPool uPool) constructor.
	 */
	public TxHandler(UTXOPool utxoPool) {
		// IMPLEMENT THIS
        this.utxoPool = new UTXOPool(utxoPool);
        txArrays = new Transaction[txMaxSize];
	}

	/* Returns true if 
	 * (1) all outputs claimed by tx are in the current UTXO pool, 
	 * (2) the signatures on each input of tx are valid,
	 * (3) no UTXO is claimed multiple times by tx,
	 * (4) all of tx’s output values are non-negative, and
	 * (5) the sum of tx’s input values is greater than or equal to the sum of   
	        its output values;
	   and false otherwise.
	 */

	public boolean isValidTx(Transaction tx) {
		// IMPLEMENT THIS
	    // check (1) all inputs are in the current UTXO pool

        ArrayList<Transaction.Input> inputs = tx.getInputs();
		ArrayList<Transaction.Output> outputs = tx.getOutputs();
		for (Transaction.Input input: inputs) {
		    UTXO inputUtxo = new UTXO(input.prevTxHash, input.outputIndex);
            if (!(utxoPool.contains(inputUtxo))) {
                return false;
            }
        }

        // check (2) all inputs that comes out or previous transaction, the
        // previous output's address, this input's signature, and this inputs
        int numInput = tx.numInputs();
        for (int iInput = 0; iInput < numInput; iInput += 1) {
            Transaction.Input input = tx.getInput(iInput);
            UTXO inputUtxo = new UTXO(input.prevTxHash, input.outputIndex);
            Transaction.Output prevOutput = utxoPool.getTxOutput(inputUtxo);
            if (!(prevOutput.address.verifySignature(tx.getRawDataToSign(iInput), tx.getInput(iInput).signature))) {
                return false;
            }
        }
        // check (3)
        ArrayList<UTXO> utxos = new ArrayList<>();
        for (Transaction.Input input : inputs) {
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            if (utxos.contains(utxo)) {
                return false;
            } else {
                utxos.add(utxo);
            }
        }

        // check (4)
        for (Transaction.Output output : outputs) {
            if (output.value < 0) {
                return false;
            }
        }

        // check (5)

        int sumOutput = 0;
	    int sumInput = 0;
	    for (Transaction.Input input : inputs) {
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            Transaction.Output prevoutput = utxoPool.getTxOutput(utxo);
            sumInput += prevoutput.value;
        }
        for (Transaction.Output output : outputs) {
	        sumOutput += output.value;
        }
        return (sumInput >= sumOutput);
	}

	/* check if all outputs in tx1 does not interface with tx2
	*  to avoid double or triple spend.
	* */
	private boolean isValidPair (Transaction tx1, Transaction tx2) {
	    ArrayList<UTXO> tx1Utxos = new ArrayList<>();
       for (Transaction.Input input : tx1.getInputs()) {
           UTXO tx1Utxo = new UTXO(input.prevTxHash, input.outputIndex);
           tx1Utxos.add(tx1Utxo);
       }
       for (Transaction.Input input2 : tx2.getInputs()) {
           UTXO tx2Utxo = new UTXO(input2.prevTxHash, input2.outputIndex);
           if (tx1Utxos.contains(tx2Utxo)) {
               return false;
           }
       }
       return true;
    }

	/* Handles each epoch by receiving an unordered array of proposed 
	 * transactions, checking each transaction for correctness, 
	 * returning a mutually valid array of accepted transactions, 
	 * and updating the current UTXO pool as appropriate.
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs) {
		// IMPLEMENT THIS
        int numTxsMutualValid = 0;
        Transaction[] txsInHandler = new Transaction[txMaxSize];
        int len = possibleTxs.length;
        for (int i = 0; i < len; i += 1) {
            Transaction tx = possibleTxs[i];
            if (isValidTx(possibleTxs[i])) {
                boolean ithMutualValid = true;
                for (int j = 0; j < numTxsMutualValid; j += 1) {
                    if (!(isValidPair(txsInHandler[j], tx))) {
                        ithMutualValid = false;
                        break;
                    }
                }
                if (ithMutualValid) {
                    txsInHandler[numTxsMutualValid] = tx;
                    numTxsMutualValid += 1;
                }
            }
            if (numTxsMutualValid == len) {
                break;
            }
        }
        return txsInHandler;
	}

} 
