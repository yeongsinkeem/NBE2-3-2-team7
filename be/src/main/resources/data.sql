INSERT INTO users (email, password, name, tel, brand, profile_image, registered_at)
VALUES ('yubanghyeon@gmail.com',
        'yubanghyeon@gmail.com',  -- 비밀번호를 이메일과 동일하게 설정
        '유방현',
        '010-1234-5678',
        '테스트 브랜드',
        'brand_default.png',  -- 프로필 이미지 경로 설정
        CURRENT_TIMESTAMP());