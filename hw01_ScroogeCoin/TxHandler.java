import java.lang.reflect.Array;
import java.security.PublicKey;
import java.security.interfaces.*;
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
	 * // (3) ensure the double spend
	 * (3) no UTXO is claimed multiple times by tx, 
	 * (4) all of tx’s output values are non-negative, and
	 * (5) the sum of tx’s input values is greater than or equal to the sum of   
	        its output values;
	   and false otherwise.
	 */

	public boolean isValidTx(Transaction tx) {
		// IMPLEMENT THIS
	    byte[] txHash = tx.getHash();
	    int numOutput = tx.numOutputs();
	    // check (1)
		for (Transaction.Output output : tx.getOutputs()) {
            if (!(utxoPool.containsOutput(output))) {
                return false;
            }
        }
        ArrayList<Transaction.Input> inputs = tx.getInputs();
		ArrayList<Transaction.Output> outputs = tx.getOutputs();

        // check (2)
        for (Transaction.Input input : inputs) {
		    for (Transaction.Output output : outputs) {
		        if (!(output.address.verifySignature(tx.getRawDataToSign(input.outputIndex), input.signature))) {
		            return false;
                }
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

	/* Handles each epoch by receiving an unordered array of proposed 
	 * transactions, checking each transaction for correctness, 
	 * returning a mutually valid array of accepted transactions, 
	 * and updating the current UTXO pool as appropriate.
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs) {
		// IMPLEMENT THIS
        int len = possibleTxs.length;
        for (int i = 0; i < len; i += 1) {
            if (isValidTx(possibleTxs[i])) {

            }
        }
	}

} 
