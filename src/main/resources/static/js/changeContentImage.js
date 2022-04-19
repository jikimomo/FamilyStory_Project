(function(){

    //alert message 사라지게
    const alertMessage = document.getElementsByClassName('alertMessage');
    for(var i=0; i<alertMessage.length; i++){
        alertMessage[i].style.display = 'none';
    }
    const photoMessage = document.querySelector('#photoMessage');

    const handler = {
        init() {
            const photoRoute = document.querySelector('#photoRoute');
            const preview = document.querySelector('#preview');
            photoRoute.addEventListener('change', (evt) => {
//                console.dir(photoRoute);
                preview.innerHTML = "";
                for(var i=0; i<alertMessage.length; i++){
                    alertMessage[i].style.display = 'none';
                }

                const files = Array.from(photoRoute.files);
                if(files.length <= 5){
                    var count = 0;
                    files.forEach(file => {
                        if(validation(file)){
                            count++;
                        }
                    });

                    if(files.length === count){
//                        console.log('모든 이미지가 모든 조건 해당됨');
                        var index = 0;
                        files.forEach(file => {

                            var imgBox = document.createElement("div");
                            imgBox.setAttribute("class", "imgBox");
                            imgBox.setAttribute("id", `${file.lastModified}`);
                            imgBox.setAttribute("style", "display: inline-block; margin: 12px;");
                            var preview = document.getElementById('preview');
                            preview.appendChild(imgBox);

                            var imgBtn = document.createElement("button");
                            imgBtn.setAttribute("data-index", `${file.lastModified}`);
                            imgBtn.setAttribute("class", "file-remove");
                            imgBtn.setAttribute("style", "display: block; margin-left: 75px; background-color: #fa6060; border: none; border-radius: 3px; color: #ffffff")
                            imgBtn.innerHTML="X";

                            var reader = new FileReader();
                            reader.onload = function(evt){
                                var img = document.createElement("img");
                                img.setAttribute("src", evt.target.result);
                                img.setAttribute("width", "170px");
                                img.setAttribute("height", "120px");
                                imgBox.appendChild(img);
                                imgBox.appendChild(imgBtn);
                            };
                            reader.readAsDataURL(file);
                        });
                    }
                    else{
                        photoRoute.value = "";
                    }
                }
                else {
                    photoMessage.innerHTML = "사진은 최대 5개까지 추가할 수 있습니다.";
                    photoMessage.style.display = 'block';
                    preview.innerHTML = "";
                    photoRoute.value = "";
                }
            });
        },

        removeFile: () => {
            document.addEventListener('click', (e) => {
            if(e.target.className !== 'file-remove') return;
            const removeTargetId = e.target.dataset.index;
            const removeTarget = document.getElementById(removeTargetId);
            const files = document.querySelector('#photoRoute').files;
            const dataTranster = new DataTransfer();

            Array.from(files)
                .filter(file => file.lastModified != removeTargetId)
                .forEach(file => {
                    dataTranster.items.add(file);
                });

            document.querySelector('#photoRoute').files = dataTranster.files;

            removeTarget.remove();

            const photoRoute = document.querySelector('#photoRoute');
//            console.dir(photoRoute);
            })
        }
    }

    handler.init()
    handler.removeFile()

    /* 첨부파일 검증 */
    function validation(obj){
        const fileTypes = ['image/gif', 'image/jpeg', 'image/png', 'image/bmp', 'image/tif'];
        if (obj.name.length > 100) {
            photoMessage.innerHTML = "파일명이 100자 이상인 이미지는 추가할 수 없습니다.";
            photoMessage.style.display = 'block';
        } else if (obj.size > (10 * 1024 * 1024)) {
            photoMessage.innerHTML = "최대 파일 용량인 10MB를 초과한 이미지 파일은 추가할 수 없습니다.";
            photoMessage.style.display = 'block';
            return false;
        } else if (obj.name.lastIndexOf('.') == -1) {
            photoMessage.innerHTML = "확장자가 없는 파일은 추가할 수 없습니다.";
            photoMessage.style.display = 'block';
            return false;
        } else if (!fileTypes.includes(obj.type)) {
            photoMessage.innerHTML = "이미지가 아닌 파일은 추가할 수 없습니다.";
            photoMessage.style.display = 'block';
            return false;
        } else {
            return true;
        }
    }

})();