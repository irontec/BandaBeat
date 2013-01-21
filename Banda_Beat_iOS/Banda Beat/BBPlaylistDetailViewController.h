//
//  BBPlaylistDetailViewController.h
//  Banda Beat
//
//  Created by iker on 27/11/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GAITrackedViewController.h"

@interface BBPlaylistDetailViewController : GAITrackedViewController
@property(weak,nonatomic) IBOutlet UITableView *tableView;
@property(strong,nonatomic) NSMutableArray *tracks;
@property(strong,nonatomic) NSNumber *idPlaylist;
@property(strong,nonatomic) NSString *playlistTitle;
@end
