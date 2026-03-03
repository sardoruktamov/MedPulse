package api.medpulse.uz.service;

import api.medpulse.uz.dto.AppResponse;
import api.medpulse.uz.dto.FilterResultDTO;
import api.medpulse.uz.dto.ProfileDTO;
import api.medpulse.uz.entity.PostAttachEntity;
import api.medpulse.uz.entity.PostEntity;
import api.medpulse.uz.enums.ActionType;
import api.medpulse.uz.enums.GeneralStatus;
import api.medpulse.uz.enums.ProfileRole;
import api.medpulse.uz.exps.AppBadException;
import api.medpulse.uz.repository.CustomPostRepository;
import api.medpulse.uz.repository.PostAttachRepository;
import api.medpulse.uz.repository.PostRepository;
import api.medpulse.uz.util.SpringSecurityUtil;
import api.medpulse.uz.dto.post.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AttachService attachService;
    @Autowired
    private CustomPostRepository customPostRepository;
    @Autowired
    private LogService logService;
    @Autowired
    private PostAttachRepository postAttachRepository;

    public PostDTO create(PostCreateDTO dto) {
        if (dto.getAttachIdList() != null && dto.getAttachIdList().size() > 4) {
            throw new AppBadException("Maksimal 4 ta media yuklash mumkin!");
        }

        PostEntity entity = new PostEntity();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setCreatedDate(LocalDateTime.now());
        entity.setVisible(true);
        entity.setStatus(GeneralStatus.BLOCK);
        entity.setProfileId(SpringSecurityUtil.getCurrentUserId());
        postRepository.save(entity);

        if (dto.getAttachIdList() != null) {
            for (String attachId : dto.getAttachIdList()) {
                PostAttachEntity postAttach = new PostAttachEntity();
                postAttach.setPostId(entity.getId());
                postAttach.setAttachId(attachId);
                postAttachRepository.save(postAttach);
            }
        }

        return toDto(entity);
    }

    public Page<PostDTO> getProfilePostList(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Integer profId = SpringSecurityUtil.getCurrentUserId();
        Page<PostEntity> result = postRepository.getAllByProfileIdAndVisibleTrueOrderByCreatedDateDesc(profId,
                pageRequest);
        List<PostDTO> dtoList = result.getContent().stream()
                .map(this::toInfoDto)
                .toList();

        return new PageImpl<PostDTO>(dtoList, pageRequest, result.getTotalElements());
    }

    public PostDTO getById(String id) {
        PostEntity entity = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        return toDto(entity);
    }

    public PostDTO update(String id, PostCreateDTO dto) {
        PostEntity entity = get(id);
        boolean isAdmin = SpringSecurityUtil.hazRole(ProfileRole.ROLE_ADMIN)
                || SpringSecurityUtil.hazRole(ProfileRole.ROLE_SUPERADMIN);
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();

        if (!isAdmin && !entity.getProfileId().equals(currentUserId)) {
            throw new AppBadException("You do not have permission to update this post");
        }

        if (isAdmin) {
            logService.createAdminLog(ActionType.POST_UPDATE, id, "Admin updated post: " + id);
        }

        if (dto.getAttachIdList() != null && dto.getAttachIdList().size() > 4) {
            throw new AppBadException("Maksimal 4 ta media yuklash mumkin!");
        }

        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        postRepository.save(entity);

        // Update media list
        postAttachRepository.deleteByPostId(id);
        if (dto.getAttachIdList() != null) {
            for (String attachId : dto.getAttachIdList()) {
                PostAttachEntity postAttach = new PostAttachEntity();
                postAttach.setPostId(entity.getId());
                postAttach.setAttachId(attachId);
                postAttachRepository.save(postAttach);
            }
        }
        return toDto(entity);
    }

    public AppResponse<String> delete(String id) {
        PostEntity entity = get(id);
        boolean isAdmin = SpringSecurityUtil.hazRole(ProfileRole.ROLE_ADMIN)
                || SpringSecurityUtil.hazRole(ProfileRole.ROLE_SUPERADMIN);
        Integer currentUserId = SpringSecurityUtil.getCurrentUserId();

        if (!isAdmin && !entity.getProfileId().equals(currentUserId)) {
            throw new AppBadException("You do not have permission to delete this post");
        }

        if (isAdmin) {
            logService.createAdminLog(ActionType.POST_DELETE, id, "Admin deleted post: " + id);
        }

        postRepository.delete(id);
        return new AppResponse<>("Post muvoffaqiyatli o'chirildi.");
    }

    public PageImpl<PostDTO> filter(PostFilterDTO dto, int page, int size) {
        FilterResultDTO<PostEntity> resultDto = customPostRepository.filter(dto, page, size);
        List<PostDTO> dtoList = resultDto.getList().stream()
                .map(this::toInfoDto)
                .toList();
        return new PageImpl<>(dtoList, PageRequest.of(page, size), resultDto.getTotalCount());
    }

    public PageImpl<PostDTO> adminFilter(PostAdminFilterDTO dto, int page, int size) {
        FilterResultDTO<Object[]> resultDto = customPostRepository.filter(dto, page, size);
        List<PostDTO> dtoList = resultDto.getList().stream()
                .map(this::toDto)
                .toList();
        return new PageImpl<>(dtoList, PageRequest.of(page, size), resultDto.getTotalCount());
    }

    public PostDTO toDto(Object[] obj) {
        PostDTO post = new PostDTO();
        post.setId((String) obj[0]);
        post.setTitle((String) obj[1]);
        post.setCreatedDate((LocalDateTime) obj[3]);

        ProfileDTO profile = new ProfileDTO();
        profile.setId((Integer) obj[4]);
        profile.setName((String) obj[5]);
        profile.setUsername((String) obj[6]);
        post.setProfile(profile);

        List<PostAttachEntity> attachEntities = postAttachRepository.findAllByPostId(post.getId());
        List<api.medpulse.uz.dto.AttachDTO> mediaList = attachEntities.stream()
                .map(pa -> attachService.attachDTO(pa.getAttachId()))
                .toList();
        post.setMediaList(mediaList);

        return post;
    }

    public List<PostDTO> getSimilarPostList(SimilarPostListDTO dto) {
        List<PostEntity> postEntitiesList = postRepository.getSimilarPostList(dto.getExceptId());

        List<PostDTO> dtoList = postEntitiesList.stream()
                .map(this::toInfoDto)
                .toList();
        return dtoList;
    }

    public PostDTO toDto(PostEntity entity) {
        PostDTO dto = new PostDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setCreatedDate(entity.getCreatedDate());

        List<PostAttachEntity> attachEntities = postAttachRepository.findAllByPostId(entity.getId());
        List<api.medpulse.uz.dto.AttachDTO> mediaList = attachEntities.stream()
                .map(pa -> attachService.attachDTO(pa.getAttachId()))
                .toList();
        dto.setMediaList(mediaList);

        return dto;
    }

    public PostDTO toInfoDto(PostEntity entity) {
        PostDTO dto = new PostDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setCreatedDate(entity.getCreatedDate());

        List<PostAttachEntity> attachEntities = postAttachRepository.findAllByPostId(entity.getId());
        List<api.medpulse.uz.dto.AttachDTO> mediaList = attachEntities.stream()
                .map(pa -> attachService.attachDTO(pa.getAttachId()))
                .toList();
        dto.setMediaList(mediaList);

        return dto;
    }

    public PostEntity get(String id) {
        return postRepository.findById(id).orElseThrow(() -> new AppBadException("Post not found"));
    }
}
