uniform sampler2D src_tex_unit0;
uniform vec2 src_tex_offset0;

void main(void)
{
    vec4 color = texture2D(src_tex_unit0, gl_TexCoord[0].st);
         // Convert to grayscale using NTSC conversion weights
    float gray = dot(vec3(color.r, color.g, color.b), vec3(0.299, 0.587, 0.114));
    
    gl_FragColor = vec4(gray, gray, gray, gl_Color.a);
}
