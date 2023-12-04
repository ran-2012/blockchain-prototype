
## Procedure

### First time use case

- User record some data
- Node check if exists in local chain 
- If not exists, ask Global chain if exists
- If not exists, Global chain return "New Account" with signature and record in global chain.
- Node receive message and create transaction from address "0" to users account

### Normal case

- User record some data
- Node check if exists in local chain
- If exists, normal transaction

### Cross shard case

- User record some data
- Node check if exists in local chain
- If not exists, ask Global chain if exists
- If exists, Global chain return "existing in another shard" with signature and node url in corresponding sharding
- Node communicate with that node with user's signature 
- Receive data record on the chain
- Original chain record user send transaction to address "0"
- Current chain record address "0" send transaction to user (with signatures)

## Design

- Inter/Intra committee consensus: Simplified PBFT
  - Only one round signature collection
  - Leader are selected by predefined order
- Use Genesis block to pre-generate some user account/data
- Use HTTP
- 3 sharding with 4 nodes in each sharding
- 3 nodes in Global chain
- On global chain:
  - User location
  - Block hash in local chains
- On local chain:
  - User transaction
