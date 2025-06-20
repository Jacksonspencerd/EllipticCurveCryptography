# EllipticCurveCryptography
Public-key encryption system using the NUMS ed-256-mers* Edwards elliptic curve, built from scratch in Java with AES-GCM for authenticated encryption.

This project implements a complete public-key encryption system in Java using the NUMS ed-256-mers* Edwards elliptic curve. It includes custom elliptic curve arithmetic, key generation from passphrases, encryption/decryption based on DHIES (Diffie-Hellman Integrated Encryption Scheme), and AES-GCM for authenticated symmetric encryption.

## Features

- Implements Edwards-form elliptic curve over a 256-bit prime field
- Computes generator point from curve equation using modular square root
- Supports point addition, negation, and double-and-add scalar multiplication
- Generates key pairs from passphrases via SHA-512 hashing
- Encrypts messages using ephemeral ECDH + AES-GCM (substituting for Ascon)
- Decrypts messages using derived shared secrets from public keys

## Technologies Used

- Java
- Modular arithmetic with `BigInteger`
- AES-GCM (Java Cryptography Extension)
- SHA-512 and SHA-256 for hashing and key derivation
- Base64 encoding for portable ciphertext
