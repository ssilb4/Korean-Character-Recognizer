package org.adroidtown.automata;

import java.io.IOException;

public class Pattern {
    int shape = 2;
    int length_check = 1;
    int beforecount = 0;
    static final int wide = 0;
    static final int high = 1;
    static final int square = 2;
    static int difference = 1;

    int before_distance[] = {0,0};

    int state_consonant_count = 2;
    int beforeminX = 9999;
    int beforemaxX = 0;
    int beforeminY = 9999;
    int beforemaxY = 0;

    int starmax = 0;
    int beforedistance = 0;

    int before_count[] = {0,0};
    public String makeChainCode(String args) throws IOException {
        System.out.println("args = " + args);
        // TODO Auto-generated method stub
        int length = -1;
        char[] chaincode = new char[1000];                      //문자열을 위한 char[]
        int aCount = -1;                                                   //저장된 숫자의 개수
        int[] x = new int[10000];                                             //x좌표 저장
        int[] y = new int[10000];                                             //y좌표 저장
        String line = args;                                      //한 줄씩 읽음
        int tempX = 0, tempY = 0;                                          //tempX은 x좌표, tempY는 y좌표 저장
        int state = 0;                                                   //상태표시
        aCount = -1;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '(') {
                state = 0;
            } else if (line.charAt(i) == ',') {
                state = 1;
            } else if (line.charAt(i) == ')') {
                state = 2;
            }
            if (state == 0) {
                if (line.charAt(i) == '-') {
                    i += 5;
                    x[aCount] = -1;
                    y[aCount] = -1;
                    continue;
                }
                if (line.charAt(i) >= '0' && line.charAt(i) <= '9') {
                    tempX *= 10;
                    tempX += line.charAt(i) - '0';
                }
            } else if (state == 1) {
                if (line.charAt(i) >= '0' && line.charAt(i) <= '9') {
                    tempY *= 10;
                    tempY += line.charAt(i) - '0';
                }
            } else if (state == 2) {
                aCount++;
                x[aCount] = tempX;
                y[aCount] = tempY;
                tempX = 0;
                tempY = 0;
                state = 0;
                if(MkChain(x,y,aCount)!='-') {
                    length++;
                    chaincode[length] = MkChain(x, y, aCount);
                }
            }
        }
        String tempstring = "";
        normalize(chaincode, length);
        normalize2(chaincode, length);
        System.out.println(chaincode);
        shape = vowelShape(x,y);
        if(state_consonant_count == 2 || state_consonant_count == 1){
            length_check = consonantShape(x,y);
        }
        makestar(chaincode, length, y);
        chaincode = makeO(chaincode, length);
        chaincode = SimpleCode(chaincode, length);
        for(int i = 0; i<chaincode.length; i++){
            if(chaincode[i] == 0 && chaincode[i+1] == 0 && chaincode[i+2] == 0)
                break;
            tempstring += Character.toString(chaincode[i]);
        }
        return tempstring;
    }


    char MkChain(int[] x, int[] y, int max){
        char var = 0;
        int xdiff = 0;         //x변화
        int ydiff = 0;         //y변화
        if(max == 0)         //점이 한 개일 경우는 체인코드를 저장할 수 없음
            return '-';
        if(x[max-1] == -1){      //떼어놓았다가 붙였을 때는 -1을 출력
            var = '-';
            System.out.print(var);
            return '-';
        }
        xdiff = x[max] - x[max-1];         //x값의 차이
        ydiff = y[max] - y[max-1];         //y값의 차이

      /*각도로 구하는 곳*/
        double angle = angle(xdiff,ydiff);
        //3: -, 4: /, 1: |, 2: \
        if(angle >= 337.5 || angle < 22.5){
            var = '1';
        }
        else if(angle >= 292.5 && angle < 337.5){
            var = '3';
        }
        else if(angle >= 247.5 && angle < 292.5){
            var = '2';
        }
        else if(angle >= 202.5 && angle < 247.5){
            var = '4';
        }
        else if(angle >= 157.5 && angle < 247.5){
            var = '1';
        }
        else if(angle >= 112.5 && angle < 157.5){
            var = '3';
        }
        else if(angle >= 67.5 && angle < 112.5){
            var = '2';
        }
        else if(angle >= 22.5 && angle < 67.5){
            var = '4';
        }
        return var;
    }


    //각도를 구하는 알고리즘
    double angle(int dx, int dy) {
        int ax = dx > 0 ? dx : -dx;
        int ay = dy > 0 ? dy : -dy;
        double t;
        if ((ax + ay) == 0) t = 0;
        else t = (double) dy / (double) (ax + ay);
        if (dx < 0) t = 2 - t;
        else if (dy < 0) t = 4 + t;
        return t * 90.0;
    }
    void normalize(char c[], int length){
        boolean reliability = false;
        for(int i = 0; i<=length; i++){
            if(i == 0 && i+2 == length || i==1 && i+2 == length){
                c[i+1] = c[i];
                c[i+2] = c[i+1];
            }
            else if(i+2 == length){
                c[i] = c[i-1];
                c[i+1] = c[i];
                c[i+2] = c[i];
                break;
            }
            else if(c[i] == '0'){
                reliability = false;
                continue;
            }
            else if(c[i+2] == '0'){
                c[i] = c[i-1];
                c[i+1] = c[i];
                reliability = false;
                i++;
                continue;
            }
            else if(c[i] == c[i+1] && c[i+1] == c[i+2]){      //전부 같을 때
                i+=2;
                reliability = true;
            }
            else if(reliability == true){            //전부 같진 않은데 이전이 다 같았을 때
                c[i] = c[i-1];
                reliability = false;
            }
            else if(c[i+2] == c[i+1]){
                c[i] = c[i+1];
                reliability = false;
            }
            else if(c[i] == c[i+1]){
                reliability = false;
                continue;
            }
            else if(c[i] == c[i+2]){
                reliability = false;
                c[i+1] = c[i];
            }
            else{
                reliability = false;
                c[i+1] = c[i];
            }
        }
    }
    void normalize2(char c[], int length){
        int ccount[] = new int[6];
        for(int i = 0; i<=length; i++){
            if(length  == 0){
                break;
            }
            else if(length  == 1){
                c[i+1] = c[i];
                break;
            }
            else if(length == 2){
                c[i+2] = c[i];
                c[i+1] = c[i];
                break;
            }

            if(i+2 == length){
                c[i] = c[i-1];
                c[i+1] = c[i];
                c[i+2] = c[i];
                break;
            }
            if(c[i] == '0'){
                continue;
            }
            else if(c[i+2] == '0'){
                c[i] = c[i-1];
                c[i+1] = c[i];
                i++;
                continue;
            }
            ccount[c[i] - '0']++;
            if(c[i] != c[i+1]){
                if(c[i+1] != c[i+2]){
                    c[i+1] = c[i];
                }
                else if(i+2 < length && c[i+1] == c[i+2] && c[i+2] != c[i+3] && c[i+3] == c[i+4]){
                    c[i+1] = c[i];
                    c[i+2] = c[i];
                }
                if(i==0){
                    c[i] = c[i+1];
                }
                else if(c[i] != c[i-1] && c[i+2] == c[i+1]){
                    c[i] = c[i+1];
                }
            }
         /*
         if(i==1 && c[i+1] == c[i+2]){
            c[i] = c[i+1];
         }
         else if(i==2 && c[i+1] == c[i+2]&& c[i-1] == c[i+2]){
            c[i] = c[i+1];
         }
         else if(c[i-2] == c[i+2] && c[i-1] == c[i+1] && c[i+1] == c[i+2]){      //전부 같을 때
            c[i] = c[i-1];
         }
         else if(c[i-2] == c[i+2] && c[i-3] == c[i+3] && c[i+3] == c[i+2]){      //전부 같을 때
            c[i] = c[i-2];
            c[i+1] = c[i-2];
         }
         */
        }
    }
    char[] makeO(char c[], int length){
        int count[] = {0,0,0,0,0};
        int start = 0;
        for(int i = 0; i<length; i++){
            if(c[i] == '*'){
                start = i+1;
                continue;
            }
            count[c[i] - '0']++;
            if(c[i] == '0' || i == length-1){
                if(count[1] != 0 && count[2] != 0 && count[3] != 0 && count[4] != 0){
                    for(int j = start; j<i; j++){
                        c[j] = '5';
                    }
                }
                start = i+1;
                count[4] = 0;
                count[1] = 0;
                count[2] = 0;
                count[3] = 0;
            }
            if(c[i] == 0)
                break;
        }
        return c;
    }
    char[] SimpleCode(char c[], int length){
        char state[] = new char[1000];
        int state_count = 0;
        int count = 0;
        char temp_state = 'x';
        temp_state = c[0];
        for(int i = 0; i<=length; i++){
            if(c[i] == '0'){
                count = 0;
                temp_state = 'x';
                state[state_count] = '0';
                state_count++;
                continue;
            }
            if(c[i] == '5'){
                if(i>0){
                    if(c[i-1] == '5')
                        continue;
                }
                state[state_count] = c[i];
                state_count++;
                count = 0;
            }
            else if(c[i] == '*'){
                count = 0;
                temp_state = 'x';
                state[state_count] = '*';
                state_count++;
                continue;
            }
            if(temp_state == c[i]){
                count++;
            }
            else{
                temp_state = c[i];
                count = 0;
            }
            if(count == 3 && state_count == 0){
                state[state_count] = temp_state;
                state_count++;
                count = 0;
            }
            else if(count == 3 && (state[state_count-1] != temp_state)){
                state[state_count] = temp_state;
                state_count++;
                count = 0;
            }
        }
        state[state_count] = '\0';
      /*
      for(int i = state_count; i<100-1; i++){
         state[1+i] = '\0';
      }
      state[state_count+1] = '\0';
      */
        //System.out.println(state);
        return state;
    }
    int vowelShape(int x[], int y[]){
        int minX = 9999, minY = 9999, maxX = 0, maxY = 0;
        int current_distance[] = {0,0};
        int shape = 3;
        final int difference = 6;
        for(int i = 0; ; i++){
            if(x[i] == -1 && y[i] == -1){      //종료조건 제대로 확인
                break;
            }
            if(x[i] > maxX){
                maxX = x[i];
            }
            if(x[i] < minX){
                minX = x[i];
            }
            if(y[i] > maxY){
                maxY = y[i];
            }
            if(y[i] < minY){
                minY = y[i];
            }
        }
        current_distance[0] = maxX - minX;
        current_distance[1] = maxY - minY;
        if(current_distance[0] > before_distance[0]){
            before_distance[0] = current_distance[0];
        }
        if(current_distance[1] > before_distance[1]){
            before_distance[1] = current_distance[1];
        }
        if(before_distance[0] > before_distance[1] + difference){
            shape = wide;
        }
        else if(before_distance[1] > before_distance[0] + difference){
            shape = high;
        }
        else{
            shape = square;
        }
        return shape;
    }
    int consonantShape(int x[], int y[]){
        final int difference = 2;
        int minX = 99999, maxX = 0;
        int minY = 99999, maxY = 0;
        if(state_consonant_count == 0)
            return 2;
        state_consonant_count--;
        for(int i = 0; ; i++){
            if(x[i] == -1){      //종료조건 제대로 확인
                break;
            }
            if(x[i] > maxX){
                maxX = x[i];
            }
            if(x[i] < minX){
                minX = x[i];
            }
            if(y[i] > maxY){
                maxY = x[i];
            }
            if(y[i] < minY){
                minY = y[i];
            }
        }
        if(state_consonant_count == 1){
            beforeminX = minX;
            beforemaxX = maxX;
            beforeminY = minY;
            beforemaxY = maxY;
        }
        else{
            //완전히 밖
            if(beforemaxX < minX || beforeminX > maxX || beforemaxY < minY || beforeminY > maxY){
                return 1;         //1이 바깥
            }
            //완전히 안
            else if(beforemaxX > maxX && beforeminX < minX && beforemaxY > maxY && beforeminY < minY){
                return 0;
            }
            else if(beforemaxX < maxX - difference && beforeminX > minX + difference){
                return 1;         //근 일 때
            }
            else if((beforemaxX + beforeminX)/2 < minX){
                return 1;         //1이 바깥
            }
            else if(beforemaxY  < minY + difference){
                return 1;         //1이 바깥
            }
            else{
                return 0;
            }
        }
        return 1;
    }
    void makestar(char c[], int length, int y[]){
        int ccount = 0;
        if(ccount == -1){
            return;
        }
        if(starmax == -1){
            return;
        }
        if(starmax != 0 && starmax < y[0] && shape == 1){
            c[0] = '*';
            starmax = -1;
            return;
        }
        else if(starmax != 0 && starmax < y[0] ){
            ccount = -1;
            return;
        }
        for(int i = 0; i<length; i++){
            if(c[i] == '2'){
                ccount++;
            }
            if(ccount > 3){
                if(starmax < y[i]){
                    starmax = y[i];
                }
            }
        }
    }
}
