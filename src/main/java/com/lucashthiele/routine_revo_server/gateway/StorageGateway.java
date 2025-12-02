package com.lucashthiele.routine_revo_server.gateway;

import java.io.InputStream;

public interface StorageGateway {
  /**
   * Uploads a file to storage and returns the public URL
   * @param fileName The name of the file
   * @param content The file content as InputStream
   * @param contentType The MIME type of the file
   * @param contentLength The size of the file in bytes
   * @return The public URL of the uploaded file
   */
  String uploadFile(String fileName, InputStream content, String contentType, long contentLength);
  
  /**
   * Deletes a file from storage
   * @param fileUrl The URL of the file to delete
   */
  void deleteFile(String fileUrl);
}

