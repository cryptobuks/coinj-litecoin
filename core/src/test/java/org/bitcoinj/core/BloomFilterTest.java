/*
 * Copyright 2012 Matt Corallo
 * Copyright 2014 Andreas Schildbach
 * Copyright 2015 BitTechCenter Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitcoinj.core;

import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.KeyChainGroup;
import org.junit.Test;

import java.util.Arrays;

import static org.bitcoinj.core.Utils.HEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Date: 7/17/15
 * Time: 5:29 PM
 *
 * @author Mikhail Kulikov
 */
public class BloomFilterTest {

    @Test
    public void walletTest() throws Exception {
        NetworkParameters params = MainNetParams.get();

        DumpedPrivateKey privKey = new DumpedPrivateKey(params, "6vyk9uiGUm8CCKbYue4PpoSbdWKZnjrxMQYJ1PaDGrQ4bLHTxQJ");

        Address addr = privKey.getKey().toAddress(params);
        assertTrue(addr.toString().equals("LRjuGUiW43hRmd6WTzTEhTRDQPB4Wodf6Y"));

        KeyChainGroup group = new KeyChainGroup(params);
        // Add a random key which happens to have been used in a recent generation
        group.importKeys(privKey.getKey(), ECKey.fromPublicOnly(HEX.decode("03cb219f69f1b49468bd563239a86667e74a06fcba69ac50a08a5cbc42a5808e99")));
        Wallet wallet = new Wallet(params, group);
        wallet.commitTx(new Transaction(params, HEX.decode("01000000010000000000000000000000000000000000000000000000000000000000000000ffffffff0d038754030114062f503253482fffffffff01c05e559500000000232103cb219f69f1b49468bd563239a86667e74a06fcba69ac50a08a5cbc42a5808e99ac00000000")));

        // We should have 2 per pubkey, and one for the pay-2-pubkey output we have
        assertEquals(5, wallet.getBloomFilterElementCount());

        BloomFilter filter = wallet.getBloomFilter(wallet.getBloomFilterElementCount(), 0.001, 0);

        // Value generated by the reference client
        assertTrue(Arrays.equals(HEX.decode("082ae5edc8e51d4a03080000000000000002"), filter.bitcoinSerialize()));
    }

}
