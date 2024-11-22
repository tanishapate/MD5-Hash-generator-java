import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Random;

public class DestinationHashGenerator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <roll_number> <json_file_path>");
            System.exit(1);
        }

        String rollNumber = args[0].toLowerCase().replace(" ", "");
        String filePath = args[1];

        try {
            // Step 2: Read and parse JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(filePath));

            // Step 3: Traverse JSON to find the first instance of "destination"
            String destinationValue = findDestinationValue(rootNode);
            if (destinationValue == null) {
                System.out.println("No key 'destination' found in the JSON file.");
                System.exit(1);
            }

            // Step 4: Generate random 8-character alphanumeric string
            String randomString = generateRandomString(8);

            // Step 5: Concatenate and generate MD5 hash
            String concatenatedString = rollNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(concatenatedString);

            // Step 7: Output the hash and random string
            System.out.println(md5Hash + ";" + randomString);
        } catch (IOException e) {
            System.err.println("Error reading the JSON file: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error generating MD5 hash: " + e.getMessage());
        }
    }

    // Recursive method to find the first occurrence of "destination"
    private static String findDestinationValue(JsonNode node) {
        if (node.isObject()) {
            Iterator<String> fieldNames = node.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                if (fieldName.equals("destination")) {
                    return node.get(fieldName).asText();
                }
                String result = findDestinationValue(node.get(fieldName));
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayItem : node) {
                String result = findDestinationValue(arrayItem);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    // Generate an 8-character alphanumeric random string
    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(chars.charAt(random.nextInt(chars.length())));
        }
        return result.toString();
    }

    // Generate an MD5 hash of a given string
    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
