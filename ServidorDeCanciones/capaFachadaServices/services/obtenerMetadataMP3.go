package services

import (
	"bytes"
	"fmt"
	"io"

	"github.com/tcolgate/mp3"
)

func CalcularDuracionMP3(data []byte) (float64, error) {
	r := bytes.NewReader(data)
	d := mp3.NewDecoder(r)

	var f mp3.Frame
	skipped := 0

	duration := 0.0

	for {
		if err := d.Decode(&f, &skipped); err != nil {
			if err == io.EOF {
				break
			}
			return 0, fmt.Errorf("error decodificando MP3: %v", err)
		}

		duration += f.Duration().Seconds()
	}

	return duration, nil
}
