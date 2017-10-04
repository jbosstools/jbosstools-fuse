package org.jboss.fuse.wsdl2rest.impl.writer;

/*
 * Copyright (c) 2008 SL_OpenSource Consortium
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public class MessageWriterFactory {
    private static MessageWriter _msgWriter = null;

    public static MessageWriter getMessageWriter(Class<?> msgWriter){
        if(_msgWriter == null){
            if(msgWriter == null){
                _msgWriter = new ConsoleMessageWriter();
            }else{
                Object msgW = null;
                try {
                    msgW = msgWriter.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    _msgWriter = new ConsoleMessageWriter();
                }
                if(msgW instanceof MessageWriter){
                    _msgWriter = (MessageWriter)msgW;
                }
            }
        }
        return _msgWriter;
    }
    
    public static  MessageWriter getMessageWriter(){
        if(_msgWriter == null){
             _msgWriter = new ConsoleMessageWriter();
        }
        return _msgWriter;
    }
}
