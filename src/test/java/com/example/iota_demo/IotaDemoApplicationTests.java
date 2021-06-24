package com.example.iota_demo;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.HexUtil;
import com.alibaba.fastjson.JSON;
import com.example.common.Numeric;
import com.example.common.ReadStream;
import com.example.common.WriteStream;
import com.example.transaction.*;
import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import org.bouncycastle.jcajce.provider.digest.Blake2b;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class IotaDemoApplicationTests {

    @Test
    public void contextLoads() throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, SignatureException {
        Essence essence = new Essence();
        essence.setType(0);
        Payload payload = new Payload();
        payload.setType(Payload.INDEXATION_PAYLOAD_TYPE);

        //payload.setIndex(HexUtil.encodeHexStr("i am test"));
        //payload.setData(HexUtil.encodeHexStr("测试"));

        payload.setIndex(Numeric.cleanHexPrefix(Numeric.toHexString("i am test".getBytes(StandardCharsets.UTF_8))));
        payload.setData(Numeric.cleanHexPrefix(Numeric.toHexString("测试".getBytes(StandardCharsets.UTF_8))));
        essence.setPayload(null);

        Input input = new Input();
        input.setType(0);
        input.setTransactionId("471f64e426674440983c67222720ec517f76ca321b38108bbf00f1e37df2eb5e");
        input.setTransactionOutputIndex(0);
        List<Input> inputs = new ArrayList<>();
        inputs.add(input);
        essence.setInputs(inputs);

        Output output = new Output();
        output.setType(0);
        output.setAmount(10000000L);
        Address address = new Address();
        address.setType(0);
        address.setAddress("7b2f97c7edc8ae5a96544feb98d2a15770d083beb3b518470ce6b0b074368780");
        output.setAddress(address);

        /*Output output1 = new Output();
        output1.setType(0);
        output1.setAmount(9000000L);
        Address address1 = new Address();
        address1.setType(0);
        address1.setAddress("55c3414fa53417e01d5140f37241d2f59f0848ad6697cbfe91e2d4b2e7a424a2");
        output1.setAddress(address1);
*/
        List<Output> outputs = new ArrayList<>();
        //outputs.add(output1);
        outputs.add(output);
        essence.setOutputs(outputs);

        WriteStream serialized = new WriteStream();

        Transaction tx = new Transaction();
        tx.serializeTransactionEssence(serialized, essence);
        String hex = serialized.finalHex();
        System.out.println("hex：" + hex);
        System.err.println(JSON.toJSON(essence).toString());
        //下面是签名
        Blake2b.Blake2b256 blake2b = new Blake2b.Blake2b256();
        byte[] digest = blake2b.digest(serialized.finalBytes());

        EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
        Signature sgr = new EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));

        EdDSAPrivateKeySpec privKey = new EdDSAPrivateKeySpec(Convert.hexToBytes("b1c3c124b7abd6e2092146e82d623c8360e8c16b71a3dc0c65cf83e4921b8bdb"), spec);
        PrivateKey sKey = new EdDSAPrivateKey(privKey);

        sgr.initSign(sKey);
        sgr.setParameter(EdDSAEngine.ONE_SHOT_MODE);
        sgr.update(digest);
        byte[] sign = sgr.sign();
        String signStr = HexUtil.encodeHexStr(sign);


        System.err.println("签名：" + signStr);

        //反序列化
        Essence deserialized = tx.deserializeTransactionEssence(new ReadStream(Convert.hexToBytes(hex)));

        System.err.println(JSON.toJSON(essence).toString());
        System.err.println(JSON.toJSON(deserialized).toString());
    }

    @Test
    public void aa() {
        String index = "68656c6c6f20776f726c64";
        String data = "5370616d6d696e6720646174612e0a436f756e743a203037323935320a54696d657374616d703a20323032312d30322d31315431303a32333a34392b30313a30300a54697073656c656374696f6e3a203934c2b573";
        byte[] indexBytes = Numeric.hexStringToByteArray(index);
        byte[] dataBytes = Numeric.hexStringToByteArray(data);
        String s = new String(indexBytes, StandardCharsets.UTF_8);
        String s1 = new String(dataBytes, StandardCharsets.UTF_8);
        System.out.println(s);
        System.out.println(s1);
    }
}
