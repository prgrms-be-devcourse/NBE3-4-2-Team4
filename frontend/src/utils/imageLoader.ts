import { ImageLoader } from "next/image";

const imageLoader: ImageLoader = ({ src }) => {
    return src; // 모든 URL을 그대로 반환
};

export default imageLoader;