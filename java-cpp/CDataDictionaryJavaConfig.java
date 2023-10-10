import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(
        target = "CDataDictionaryJavaToCppExample",
        value = @Platform(
                include = {
                        "CDataCppBridge.h"
                },
                includepath = {
                        "/home/asus/mambaforge/envs/pyarrow-dev/include/"
                },
                compiler = {"cpp17"},
                linkpath = {"/home/asus/github/fork/arrow/cpp/build/debug/"},
                link = {"arrow"}
        )
)
public class CDataDictionaryJavaConfig implements InfoMapper {

    @Override
    public void map(InfoMap infoMap) {
    }
}
