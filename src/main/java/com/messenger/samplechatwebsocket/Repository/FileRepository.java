package com.messenger.samplechatwebsocket.Repository;

import com.messenger.samplechatwebsocket.Entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity,Long> {
    // Найти все файлы, загруженные конкретным пользователем
    List<FileEntity> findByUploader(String uploader);

    // Найти файл по оригинальному имени (может вернуть несколько)
    List<FileEntity> findByOriginalName(String originalName);

    // Найти файл по id и загрузчику (удобно для проверки прав)
    Optional<FileEntity> findByIdAndUploader(Long id, String uploader);


}
