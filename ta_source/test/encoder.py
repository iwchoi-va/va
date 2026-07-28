#!/usr/bin/env python
#-*- coding: utf-8 -*-

import base64

def main() :
    text1 = "aigdbta"
    text2 = "aha-0987!@"

    encoded_text = base64.encodestring(text1)
    print encoded_text
    encoded_text = base64.encodestring(text2)
    print encoded_text

    #decoded_text = base64.decodestring(encoded_text)
    #print decoded_text

    return

if __name__ == "__main__" :
    main()
