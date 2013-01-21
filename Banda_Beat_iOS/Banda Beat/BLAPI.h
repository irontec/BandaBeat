//
//  BLAPI.h
//  Banden Lehia
//
//  Created by Iker Mendilibar on 29/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BLAPI : NSObject

typedef void (^BLAPICompletionHandler)(NSString *methodName, id methodResult);
typedef void (^BLAPILoginHandler)(Boolean isLogged, id methodResult);

#pragma mark - User information
//GeneralToken sirve para saber si alguna lista ha cambiado.
@property(strong, nonatomic) NSString *generalToken;
@property(strong, nonatomic) NSString *token;
@property(nonatomic) NSNumber *userId;
@property(strong, nonatomic) NSString *username;
@property(nonatomic) BOOL isLoginDataLoaded;


+(BLAPI*)sharedInstance;
-(void)loginWithUsername:(NSString*)username password:(NSString*)password onCompletedHandler:(BLAPILoginHandler)handler;
-(void)getAllPlaylistWithHandler:(BLAPICompletionHandler)handler;
-(void)getPublicPlaylistWithHandler:(BLAPICompletionHandler)handler;
-(void)getSongsFromPlaylist:(NSNumber*)idPlaylist withHandler:(BLAPICompletionHandler)handler;
-(void)getPublicSongs:(NSNumber*)idPlaylist withHandler:(BLAPICompletionHandler)handler;
-(void)checkPlaylistUpdateWithHandler:(BLAPICompletionHandler)handler;
-(void)logout;

#pragma mark - Preferences Methods
-(void)saveLoginPreferences;
@end
