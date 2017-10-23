import java.security.PublicKey;
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
	 * // don't understand the third request??
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
	    for (int i = 0; i < numOutput; i += 1) {
	        UTXO utxo = new UTXO(txHash, i);
	        if (!(utxoPool.contains(utxo))) {
	            return false;
            }
        }
        // check (2), (4), (5)
        int sumOutput = 0;
	    int sumInput = 0;
        for (int index = 0; index < numOutput; index += 1) {
	        Transaction.Input input = tx.getInput(index);
	        sumInput += input.prevTxHash
	        Transaction.Output output = tx.getOutput(index);
	        byte[] inputTxHash = tx.getRawDataToSign(index);
	        RSAKey pubkey = (RSAKey)output.address;
            if (!(pubkey.verifySignature(inputTxHash, input.signature))) {
                return false;
            } if (output.value < 0) {
                return false;
            }
       }


	}

	/* Handles each epoch by receiving an unordered array of proposed 
	 * transactions, checking each transaction for correctness, 
	 * returning a mutually valid array of accepted transactions, 
	 * and updating the current UTXO pool as appropriate.
	 */
	public Transaction[] handleTxs(Transaction[] possibleTxs) {
		// IMPLEMENT THIS
		return null;
	}

} 
