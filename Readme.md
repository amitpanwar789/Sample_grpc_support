# gRPC Support Project in Zap

This is a sample repository for a gRPC support project in Zap. Currently, it provides functionality to decode and encode Protocol Buffer messages.

However, the decoding support is not 100% accurate for all types. Decoding of double and float values is approximate, as it attempts to determine if the values are floats or doubles by checking their exponent bits and attempting to form a float or double number. If not possible, it assumes they may be integers or longs. Additionally, for wire type 2 cases, it assumes them as nested messages and tries to decode them recursively. If any error or edge case is encountered, it assumes it as a string.

## Features

- **Encoding Support**: Supports encoding of message fields with types string, int32, int64, fixed32, and fixed64.
- **Decoding Support**: Provides decoding support for all types except repeated packed and signed integers encoded with the zigzag encoding algorithm.
- **Attached .proto File**: Includes a .proto file with the values used in the Protocol Buffer hash in the `demo.java` file.

## Code Quality Disclaimer

Sorry for code quality &#x1F614; in this repository. This is an initial implementation, and there are plans to refactor it for improved readability and maintainability.

## Usage

1. Clone the repository: `git clone https://github.com/amitpanwar789/Sample_grpc_support.git`
2. Navigate to the cloned directory: `cd Sample_grpc_support`


