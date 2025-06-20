# IM Demo（HarmonyOS）

本文主要介绍如何快速运行腾讯云即时通信 IM Demo（HarmonyOS）。

如果您需要更详细的运行指南，请 [查看完整的运行文档](https://cloud.tencent.com/document/product/269/103557)。

## 环境要求

- DevEco Studio 5.0.0 及以上

- HarmonyOS SDK API 12 及以上

## 操作步骤

### 步骤 1：创建应用

1. 登录 [即时通信 IM 控制台](https://console.cloud.tencent.com/im)。

   > **说明**
   >
   > - 如果您已有应用，请记录其 SDKAppID 信息， 直接跳到 [步骤 2：获取 SDKAppID 和密钥信息](#步骤-2获取-sdkappid-和密钥信息)。
   > - 单个腾讯云账号最多可创建 300 个应用。若已有 300 个应用，您可以先 [停用并删除](https://cloud.tencent.com/document/product/269/32578#.E5.81.9C.E7.94.A8.2F.E5.88.A0.E9.99.A4.E5.BA.94.E7.94.A8) 不再使用的应用后，再创建新的应用。**应用删除后，该 SDKAppID 对应的所有数据和服务不可恢复，请谨慎操作。**

2. 在**应用管理**页面，单击**创建新应用**。

   ![](https://im.sdk.qcloud.com/doc/HarmonyOS/doc-image/1.png)

3. 输入应用名称、选择合适的数据中心，单击**确定**即可完成应用的创建。

   ![](https://im.sdk.qcloud.com/doc/HarmonyOS/doc-image/2.png)

### 步骤 2：获取 SDKAppID 和密钥信息

1. 在**应用管理**页面的 SDKAppID 列获取 SDKAppID 信息。

   ![](https://im.sdk.qcloud.com/doc/HarmonyOS/doc-image/3.png.jpeg)

2. 在操作列单击**查看密钥**，随后在弹出的对话框中，单击**显示密钥**，复制显示后的密钥信息。

   ![](https://im.sdk.qcloud.com/doc/HarmonyOS/doc-image/4.png.jpeg)

   > **注意**
   >
   > 密钥信息为敏感信息，为防止他人盗用，请妥善保管，谨防泄露。

### 步骤 3：下载并配置 Demo 源码

1. 下载即时通信 IM Demo 工程，具体下载地址请参见 [SDK 下载](https://cloud.tencent.com/document/product/269/36887)。

   > **说明**
   >
   > 为尊重表情设计版权，下载的 Demo 工程中不包含大表情元素切图，您可以使用自己本地表情包来配置代码。未授权使用 IM Demo 中的表情包可能会构成设计侵权。

2. 打开所属终端目录的工程，找到对应的 `GenerateTestUserSig.ts` 文件（路径：`TIMSDK/HarmonyOS/UIKitDemo/uikit-next/harmony/entry/src/main/ets/signature/GenerateTestUserSig.ts`）。

3. 设置 `GenerateTestUserSig.ts` 文件中的相关参数：

- SDKAPPID：请设置为 [步骤 1](#步骤-1创建应用) 中获取的实际应用 SDKAppID。

- SECRETKEY：请设置为 [步骤 2](#步骤-2获取-sdkappid-和密钥信息) 中获取的实际密钥信息。

  ![](https://im.sdk.qcloud.com/doc/HarmonyOS/doc-image/5.png)

  > **注意**
  >
  > 本文提到的获取 UserSig 的方案是在客户端代码中配置 SECRETKEY，该方法中 SECRETKEY 很容易被反编译逆向破解，一旦您的密钥泄露，攻击者就可以盗用您的腾讯云流量，因此**该方法仅适合本地跑通 Demo 和功能调试**。正确的 UserSig 签发方式是将 UserSig 的计算代码集成到您的服务端，并提供面向 App 的接口，在需要 UserSig 时由您的 App 向业务服务器发起请求获取动态 UserSig。更多详情请参见 [服务端生成 UserSig](https://cloud.tencent.com/document/product/269/32688#GeneratingdynamicUserSig)。

### 步骤 4：编译运行

1. 打开 鸿蒙 NextDemo（路径：`TIMSDK/HarmonyOS/UIKitDemo/uikit-next/harmony`），配置签名：

   ![](https://im.sdk.qcloud.com/doc/HarmonyOS/doc-image/6.png)

   ![](https://im.sdk.qcloud.com/doc/HarmonyOS/doc-image/7.png)

2. 连接真机，编译运行：  ohpm install

   ![](https://im.sdk.qcloud.com/doc/HarmonyOS/doc-image/8.png)

## 交流与反馈

[点此进入 IM 社群](https://zhiliao.qq.com/s/c5GY7HIM62CK)，享有专业工程师的支持，解决您的难题。

## 相关链接

- [集成 IM SDK（HarmonyOS）](https://cloud.tencent.com/document/product/269/103558)

- [API 文档（HarmonyOS）](https://cloud.tencent.com/document/product/269/103559)
