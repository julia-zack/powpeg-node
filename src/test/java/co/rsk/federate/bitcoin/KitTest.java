package co.rsk.federate.bitcoin;

import co.rsk.config.BridgeConstants;
import co.rsk.config.BridgeRegTestConstants;
import co.rsk.federate.FederatorSupport;
import co.rsk.federate.adapter.ThinConverter;
import co.rsk.federate.signing.utils.TestUtils;
import co.rsk.peg.btcLockSender.BtcLockSenderProvider;
import co.rsk.peg.pegininstructions.PeginInstructionsProvider;
import org.bitcoinj.core.*;
import org.bitcoinj.wallet.Wallet;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class KitTest {
    private static BridgeConstants bridgeConstants;
    private static NetworkParameters networkParameters;
    private static Context btcContext;

    @BeforeClass
    public static void setUpBeforeClass() {
        bridgeConstants = BridgeRegTestConstants.getInstance();
        networkParameters = ThinConverter.toOriginalInstance(bridgeConstants.getBtcParamsString());
        btcContext = new Context(networkParameters);
    }

    @Test
    public void setupConsistentWallet() throws Exception {
        BtcLockSenderProvider btcLockSenderProvider = mock(BtcLockSenderProvider.class);
        PeginInstructionsProvider peginInstructionsProvider = mock(PeginInstructionsProvider.class);
        FederatorSupport federatorSupport = mock(FederatorSupport.class);
        File pegDirectoryMock = mock(File.class);

        BitcoinWrapper bitcoinWrapper = new BitcoinWrapperImpl(
            btcContext,
            bridgeConstants,
            btcLockSenderProvider,
            peginInstructionsProvider,
            federatorSupport,
            new Kit(btcContext, pegDirectoryMock, "")
        );
        List<PeerAddress> peerAddresses = new ArrayList<>();
        bitcoinWrapper.setup(peerAddresses);
        Kit kit = TestUtils.getInternalState(bitcoinWrapper, "kit");
        PeerGroup vPeerGroupMock = mock(PeerGroup.class);
        TestUtils.setInternalState(kit, "vPeerGroup", vPeerGroupMock);
        BlockChain vChainMock = mock(BlockChain.class);
        TestUtils.setInternalState(kit, "vChain", vChainMock);

        Wallet vWalletMock = mock(Wallet.class);
        when(vWalletMock.isConsistent()).thenReturn(true);
        TestUtils.setInternalState(kit, "vWallet", vWalletMock);

        kit.onSetupCompleted();

        verify(vWalletMock, times(1)).isConsistent();
        verify(vWalletMock, times(0)).reset();
    }

    @Test
    public void setupNoConsistentWallet() throws Exception {
        BtcLockSenderProvider btcLockSenderProvider = mock(BtcLockSenderProvider.class);
        PeginInstructionsProvider peginInstructionsProvider = mock(PeginInstructionsProvider.class);
        FederatorSupport federatorSupport = mock(FederatorSupport.class);
        File pegDirectoryMock = mock(File.class);

        BitcoinWrapper bitcoinWrapper = new BitcoinWrapperImpl(
            btcContext,
            bridgeConstants,
            btcLockSenderProvider,
            peginInstructionsProvider,
            federatorSupport,
            new Kit(btcContext, pegDirectoryMock, "")
        );
        List<PeerAddress> peerAddresses = new ArrayList<>();
        bitcoinWrapper.setup(peerAddresses);
        Kit kit = TestUtils.getInternalState(bitcoinWrapper, "kit");
        PeerGroup vPeerGroupMock = mock(PeerGroup.class);
        TestUtils.setInternalState(kit, "vPeerGroup", vPeerGroupMock);
        BlockChain vChainMock = mock(BlockChain.class);
        TestUtils.setInternalState(kit, "vChain", vChainMock);

        Wallet vWalletMock = mock(Wallet.class);
        when(vWalletMock.isConsistent()).thenReturn(false);
        TestUtils.setInternalState(kit, "vWallet", vWalletMock);

        kit.onSetupCompleted();

        verify(vWalletMock, times(1)).isConsistent();
        verify(vWalletMock, times(1)).reset();
    }
}
