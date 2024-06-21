import React, { useState } from 'react';
import axios from 'axios';

function SpeechComponent() {
    const [text, setText] = useState('');
    const [audioBlob, setAudioBlob] = useState(null);

    const handleSynthesize = async () => {
        try {
            const response = await axios.post('http://localhost:8080/synthesize', { text }, {
                responseType: 'arraybuffer' // 바이너리 데이터를 받을 수 있도록 설정
            });
            const audioBlob = new Blob([response.data], { type: 'audio/mp3' });
            const audioUrl = URL.createObjectURL(audioBlob);
            const audio = new Audio(audioUrl);
            audio.play();
        } catch (error) {
            console.error('Error during synthesis:', error);
            alert('Error during synthesis: ' + error.message);
        }
    };

    const handleRecognize = async () => {
        if (!audioBlob) {
            alert('Please select an audio file first');
            return;
        }

        const formData = new FormData();
        formData.append('upload', audioBlob);

        try {
            const response = await axios.post('http://localhost:8080/fileUpload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            console.log('Recognition result: ', response.data);
        } catch (error) {
            console.error('Error during recognition:', error);
            alert('Error during recognition: ' + error.message);
        }
    };

    return (
        <div>
            <h2>Clova TTS Test</h2>
            <input type="text" value={text} onChange={e => setText(e.target.value)} /><br/>
            <button onClick={handleSynthesize}>Convert to Speech</button>
            <hr/>
            <h2>Clova STT Test</h2>
            <input type="file" onChange={e => setAudioBlob(e.target.files[0])} /><br/>
            <button onClick={handleRecognize}>Recognize Speech</button>
        </div>
    );
}

export default SpeechComponent;
